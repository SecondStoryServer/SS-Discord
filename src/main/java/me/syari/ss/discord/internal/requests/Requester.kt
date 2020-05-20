package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.internal.JDA;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Requester {
    public static final String DISCORD_API_PREFIX = "https://discordapp.com/api/v6/";
    public static final String USER_AGENT = "SS-Discord";
    public static final RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final JDA api;
    private final RateLimiter rateLimiter = new RateLimiter(this);

    private final OkHttpClient httpClient;

    public Requester(@NotNull JDA api) {
        this.api = api;
        this.httpClient = api.getHttpClient();
    }

    public JDA getJDA() {
        return api;
    }

    public <T> void request(@NotNull Request<T> apiRequest) {
        if (apiRequest.shouldQueue()) {
            rateLimiter.queueRequest(apiRequest);
        } else {
            execute(apiRequest, true);
        }
    }

    private static boolean isRetry(Throwable ex) {
        return ex instanceof SocketException || ex instanceof SocketTimeoutException || ex instanceof SSLPeerUnverifiedException;
    }

    public Long execute(Request<?> apiRequest, boolean handleOnRateLimit) {
        return execute(apiRequest, false, handleOnRateLimit);
    }

    public Long execute(@NotNull Request<?> apiRequest, boolean retried, boolean handleOnRatelimit) {
        Route route = apiRequest.getRoute();
        Long retryAfter = rateLimiter.getRateLimit(route);
        if (retryAfter != null && retryAfter > 0) {
            if (handleOnRatelimit) apiRequest.handleResponse(new Response(retryAfter));
            return retryAfter;
        }
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        String url = DISCORD_API_PREFIX + route.getRoute();
        builder.url(url);
        String method = apiRequest.getRoute().getMethod().toString();
        RequestBody body = apiRequest.getBody();
        if (body == null && HttpMethod.requiresRequestBody(method)) body = EMPTY_BODY;
        builder.method(method, body)
                .header("X-RateLimit-Precision", "millisecond")
                .header("user-agent", USER_AGENT)
                .header("accept-encoding", "gzip");
        if (url.startsWith(DISCORD_API_PREFIX)) builder.header("authorization", api.getToken());
        okhttp3.Request request = builder.build();
        okhttp3.Response[] responses = new okhttp3.Response[4];
        okhttp3.Response lastResponse = null;
        try {
            int attempt = 0;
            do {
                Call call = httpClient.newCall(request);
                lastResponse = call.execute();
                responses[attempt] = lastResponse;
                if (lastResponse.code() < 500) break;
                attempt++;
                try {
                    Thread.sleep(50 * attempt);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            while (attempt < 3 && lastResponse.code() >= 500);
            if (lastResponse.code() >= 500) {
                Response response = new Response(lastResponse, -1);
                apiRequest.handleResponse(response);
                return null;
            }
            retryAfter = rateLimiter.handleResponse(route, lastResponse);
            if (retryAfter == null) {
                apiRequest.handleResponse(new Response(lastResponse, -1));
            } else if (handleOnRatelimit) {
                apiRequest.handleResponse(new Response(lastResponse, retryAfter));
            }
            return retryAfter;
        } catch (SocketTimeoutException e) {
            if (!retried) return execute(apiRequest, true, handleOnRatelimit);
            apiRequest.handleResponse(new Response(lastResponse, e));
            return null;
        } catch (Exception e) {
            if (!retried && isRetry(e)) return execute(apiRequest, true, handleOnRatelimit);
            apiRequest.handleResponse(new Response(lastResponse, e));
            return null;
        } finally {
            for (okhttp3.Response response : responses) {
                if (response == null) break;
                response.close();
            }
        }
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void shutdown() {
        rateLimiter.shutdown();
    }

}
