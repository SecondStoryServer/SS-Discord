

package me.syari.ss.discord.api.events.http;

import me.syari.ss.discord.api.events.Event;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.Route.CompiledRoute;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Indicates that a {@link RestAction RestAction} has been executed.
 * 
 * <p>Depending on the request and its result not all values have to be populated.
 */
public class HttpRequestEvent extends Event
{
    private final Request<?> request;
    private final Response response;

    public HttpRequestEvent(@Nonnull final Request<?> request, @Nonnull final Response response)
    {
        super(request.getJDA());

        this.request = request;
        this.response = response;
    }

    @Nonnull
    public Request<?> getRequest()
    {
        return this.request;
    }

    @Nullable
    public RequestBody getRequestBody()
    {
        return this.request.getBody();
    }

    @Nullable
    public Object getRequestBodyRaw()
    {
        return this.request.getRawBody();
    }

    @Nullable
    public Headers getRequestHeaders()
    {
        return this.response.getRawResponse() == null ? null : this.response.getRawResponse().request().headers();
    }

    @Nullable
    public okhttp3.Request getRequestRaw()
    {
        return this.response.getRawResponse() == null ? null : this.response.getRawResponse().request();
    }

    @Nullable
    public Response getResponse()
    {
        return this.response;
    }

    @Nullable
    public ResponseBody getResponseBody()
    {
        return this.response.getRawResponse() == null ? null : this.response.getRawResponse().body();
    }

    @Nullable
    public DataArray getResponseBodyAsArray()
    {
        return this.response.getArray();
    }

    @Nullable
    public DataObject getResponseBodyAsObject()
    {
        return this.response.getObject();
    }

    @Nullable
    public String getResponseBodyAsString()
    {
        return this.response.getString();
    }

    @Nullable
    public Headers getResponseHeaders()
    {
        return this.response.getRawResponse() == null ? null : this.response.getRawResponse().headers();
    }

    @Nullable
    public okhttp3.Response getResponseRaw()
    {
        return this.response.getRawResponse();
    }

    @Nonnull
    public Set<String> getCFRays()
    {
        return this.response.getCFRays();
    }

    @Nonnull
    public RestAction<?> getRestAction()
    {
        return this.request.getRestAction();
    }

    @Nonnull
    public CompiledRoute getRoute()
    {
        return this.request.getRoute();
    }

    public boolean isRateLimit()
    {
        return this.response.isRateLimit();
    }

}
