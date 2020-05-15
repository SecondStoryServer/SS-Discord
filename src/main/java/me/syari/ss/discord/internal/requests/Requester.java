

package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDAInfo;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.ratelimit.BotRateLimiter;
import me.syari.ss.discord.internal.requests.ratelimit.ClientRateLimiter;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.MDC;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class Requester
{
    public static final Logger LOG = JDALogger.getLog(Requester.class);
    public static final String DISCORD_API_PREFIX = String.format("https://discordapp.com/api/v%d/", JDAInfo.DISCORD_REST_VERSION);
    public static final String USER_AGENT = "DiscordBot (" + JDAInfo.GITHUB + ", " + JDAInfo.VERSION + ")";
    public static final RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);
    public static final MediaType MEDIA_TYPE_JSON  = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_OCTET = MediaType.parse("application/octet-stream; charset=utf-8");

    protected final JDAImpl api;
    protected final AuthorizationConfig authConfig;
    private final RateLimiter rateLimiter;

    private final OkHttpClient httpClient;

    //when we actually set the shard info we can also set the mdc context map, before it makes no sense
    private boolean isContextReady = false;
    private ConcurrentMap<String, String> contextMap = null;

    private volatile boolean retryOnTimeout = false;

    public Requester(JDA api)
    {
        this(api, ((JDAImpl) api).getAuthorizationConfig());
    }

    public Requester(JDA api, AuthorizationConfig authConfig)
    {
        if (authConfig == null)
            throw new NullPointerException("Provided config was null!");

        this.authConfig = authConfig;
        this.api = (JDAImpl) api;
        if (authConfig.getAccountType() == AccountType.BOT)
            rateLimiter = new BotRateLimiter(this);
        else
            rateLimiter = new ClientRateLimiter(this);
        
        this.httpClient = this.api.getHttpClient();
    }

    public void setContextReady(boolean ready)
    {
        this.isContextReady = ready;
    }

    public void setContext()
    {
        if (!isContextReady)
            return;
        if (contextMap == null)
            contextMap = api.getContextMap();
        contextMap.forEach(MDC::put);
    }

    public JDAImpl getJDA()
    {
        return api;
    }

    public <T> void request(Request<T> apiRequest)
    {
        if (rateLimiter.isShutdown) 
            throw new IllegalStateException("The Requester has been shutdown! No new requests can be requested!");

        if (apiRequest.shouldQueue())
            rateLimiter.queueRequest(apiRequest);
        else
            execute(apiRequest, true);
    }

    private static boolean isRetry(Throwable e)
    {
        return e instanceof SocketException             // Socket couldn't be created or access failed
            || e instanceof SocketTimeoutException      // Connection timed out
            || e instanceof SSLPeerUnverifiedException; // SSL Certificate was wrong
    }

    public Long execute(Request<?> apiRequest)
    {
        return execute(apiRequest, false);
    }

    
    public Long execute(Request<?> apiRequest, boolean handleOnRateLimit)
    {
        return execute(apiRequest, false, handleOnRateLimit);
    }

    public Long execute(Request<?> apiRequest, boolean retried, boolean handleOnRatelimit)
    {
        Route.CompiledRoute route = apiRequest.getRoute();
        Long retryAfter = rateLimiter.getRateLimit(route);
        if (retryAfter != null && retryAfter > 0)
        {
            if (handleOnRatelimit)
                apiRequest.handleResponse(new Response(retryAfter, Collections.emptySet()));
            return retryAfter;
        }

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

        String url = DISCORD_API_PREFIX + route.getCompiledRoute();
        builder.url(url);

        String method = apiRequest.getRoute().getMethod().toString();
        RequestBody body = apiRequest.getBody();

        if (body == null && HttpMethod.requiresRequestBody(method))
            body = EMPTY_BODY;

        builder.method(method, body)
                .header("X-RateLimit-Precision", "millisecond")
                .header("user-agent", USER_AGENT)
                .header("accept-encoding", "gzip");

        //adding token to all requests to the discord api or cdn pages
        //we can check for startsWith(DISCORD_API_PREFIX) because the cdn endpoints don't need any kind of authorization
        if (url.startsWith(DISCORD_API_PREFIX))
            builder.header("authorization", api.getToken());

        // Apply custom headers like X-Audit-Log-Reason
        // If customHeaders is null this does nothing
        if (apiRequest.getHeaders() != null)
        {
            for (Entry<String, String> header : apiRequest.getHeaders().entrySet())
                builder.addHeader(header.getKey(), header.getValue());
        }

        okhttp3.Request request = builder.build();

        Set<String> rays = new LinkedHashSet<>();
        okhttp3.Response[] responses = new okhttp3.Response[4];
        // we have an array of all responses to later close them all at once
        //the response below this comment is used as the first successful response from the server
        okhttp3.Response lastResponse = null;
        try
        {
            LOG.trace("Executing request {} {}", apiRequest.getRoute().getMethod(), url);
            int attempt = 0;
            do
            {
                //If the request has been canceled via the Future, don't execute.
                //if (apiRequest.isCanceled())
                //    return null;
                Call call = httpClient.newCall(request);
                lastResponse = call.execute();
                responses[attempt] = lastResponse;
                String cfRay = lastResponse.header("CF-RAY");
                if (cfRay != null)
                    rays.add(cfRay);

                if (lastResponse.code() < 500)
                    break; // break loop, got a successful response!

                attempt++;
                LOG.debug("Requesting {} -> {} returned status {}... retrying (attempt {})",
                        apiRequest.getRoute().getMethod(),
                        url, lastResponse.code(), attempt);
                try
                {
                    Thread.sleep(50 * attempt);
                }
                catch (InterruptedException ignored) {}
            }
            while (attempt < 3 && lastResponse.code() >= 500);

            LOG.trace("Finished Request {} {} with code {}", route.getMethod(), lastResponse.request().url(), lastResponse.code());

            if (lastResponse.code() >= 500)
            {
                //Epic failure from other end. Attempted 4 times.
                Response response = new Response(lastResponse, -1, rays);
                apiRequest.handleResponse(response);
                return null;
            }

            retryAfter = rateLimiter.handleResponse(route, lastResponse);
            if (!rays.isEmpty())
                LOG.debug("Received response with following cf-rays: {}", rays);

            if (retryAfter == null)
                apiRequest.handleResponse(new Response(lastResponse, -1, rays));
            else if (handleOnRatelimit)
                apiRequest.handleResponse(new Response(lastResponse, retryAfter, rays));

            return retryAfter;
        }
        catch (SocketTimeoutException e)
        {
            if (retryOnTimeout && !retried)
                return execute(apiRequest, true, handleOnRatelimit);
            LOG.error("Requester timed out while executing a request", e);
            apiRequest.handleResponse(new Response(lastResponse, e, rays));
            return null;
        }
        catch (Exception e)
        {
            if (retryOnTimeout && !retried && isRetry(e))
                return execute(apiRequest, true, handleOnRatelimit);
            LOG.error("There was an exception while executing a REST request", e); //This originally only printed on DEBUG in 2.x
            apiRequest.handleResponse(new Response(lastResponse, e, rays));
            return null;
        }
        finally
        {
            for (okhttp3.Response r : responses)
            {
                if (r == null)
                    break;
                r.close();
            }
        }
    }

    private void applyBody(Request<?> apiRequest, okhttp3.Request.Builder builder)
    {
        String method = apiRequest.getRoute().getMethod().toString();
        RequestBody body = apiRequest.getBody();

        if (body == null && HttpMethod.requiresRequestBody(method))
            body = EMPTY_BODY;

        builder.method(method, body);
    }

    private void applyHeaders(Request<?> apiRequest, okhttp3.Request.Builder builder, boolean authorized)
    {
        builder.header("user-agent", USER_AGENT)
               .header("accept-encoding", "gzip")
               .header("x-ratelimit-precision", "millisecond");

        //adding token to all requests to the discord api or cdn pages
        //we can check for startsWith(DISCORD_API_PREFIX) because the cdn endpoints don't need any kind of authorization
        if (authorized)
            builder.header("authorization", authConfig.getToken());

        // Apply custom headers like X-Audit-Log-Reason
        // If customHeaders is null this does nothing
        if (apiRequest.getHeaders() != null)
        {
            for (Entry<String, String> header : apiRequest.getHeaders().entrySet())
                builder.addHeader(header.getKey(), header.getValue());
        }
    }

    public OkHttpClient getHttpClient()
    {
        return this.httpClient;
    }

    public RateLimiter getRateLimiter()
    {
        return rateLimiter;
    }

    public void setRetryOnTimeout(boolean retryOnTimeout)
    {
        this.retryOnTimeout = retryOnTimeout;
    }

    public void shutdown()
    {
        rateLimiter.shutdown();
    }

}
