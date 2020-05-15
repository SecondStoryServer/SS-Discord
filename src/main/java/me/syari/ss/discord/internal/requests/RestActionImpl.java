

package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.ErrorResponseException;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.RestFuture;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class RestActionImpl<T> implements RestAction<T>
{
    public static final Logger LOG = JDALogger.getLog(RestAction.class);

    private static Consumer<Object> DEFAULT_SUCCESS = o -> {};
    private static Consumer<? super Throwable> DEFAULT_FAILURE = t ->
    {
        if (LOG.isDebugEnabled())
        {
            LOG.error("RestAction queue returned failure", t);
        }
        else if (t.getCause() != null)
        {
            LOG.error("RestAction queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage(), t.getCause());
        }
        else
        {
            LOG.error("RestAction queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage());
        }
    };

    protected static boolean passContext = true;

    protected final JDAImpl api;

    private final Route.CompiledRoute route;
    private final RequestBody data;
    private final BiFunction<Response, Request<T>, T> handler;

    private Object rawData;
    private BooleanSupplier checks;

    public static void setPassContext(boolean enable)
    {
        passContext = enable;
    }

    public static boolean isPassContext()
    {
        return passContext;
    }

    public static void setDefaultFailure(final Consumer<? super Throwable> callback)
    {
        DEFAULT_FAILURE = callback == null ? t -> {} : callback;
    }

    public static void setDefaultSuccess(final Consumer<Object> callback)
    {
        DEFAULT_SUCCESS = callback == null ? t -> {} : callback;
    }

    public static Consumer<? super Throwable> getDefaultFailure()
    {
        return DEFAULT_FAILURE;
    }

    public static Consumer<Object> getDefaultSuccess()
    {
        return DEFAULT_SUCCESS;
    }

    public RestActionImpl(JDA api, Route.CompiledRoute route)
    {
        this(api, route, (RequestBody) null, null);
    }

    public RestActionImpl(JDA api, Route.CompiledRoute route, DataObject data)
    {
        this(api, route, data, null);
    }

    public RestActionImpl(JDA api, Route.CompiledRoute route, RequestBody data)
    {
        this(api, route, data, null);
    }

    public RestActionImpl(JDA api, Route.CompiledRoute route, BiFunction<Response, Request<T>, T> handler)
    {
        this(api, route, (RequestBody) null, handler);
    }

    public RestActionImpl(JDA api, Route.CompiledRoute route, DataObject data, BiFunction<Response, Request<T>, T> handler)
    {
        this(api, route, data == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, data.toString()), handler);
        this.rawData = data;
    }

    public RestActionImpl(JDA api, Route.CompiledRoute route, RequestBody data, BiFunction<Response, Request<T>, T> handler)
    {
        Checks.notNull(api, "api");
        this.api = (JDAImpl) api;
        this.route = route;
        this.data = data;
        this.handler = handler;
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return api;
    }

    @Nonnull
    @Override
    public RestAction<T> setCheck(BooleanSupplier checks)
    {
        this.checks = checks;
        return this;
    }

    @Override
    public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure)
    {
        Route.CompiledRoute route = finalizeRoute();
        Checks.notNull(route, "Route");
        RequestBody data = finalizeData();
        CaseInsensitiveMap<String, String> headers = finalizeHeaders();
        BooleanSupplier finisher = getFinisher();
        if (success == null)
            success = DEFAULT_SUCCESS;
        if (failure == null)
            failure = DEFAULT_FAILURE;
        api.getRequester().request(new Request<>(this, success, failure, finisher, true, data, rawData, route, headers));
    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue)
    {
        Route.CompiledRoute route = finalizeRoute();
        Checks.notNull(route, "Route");
        RequestBody data = finalizeData();
        CaseInsensitiveMap<String, String> headers = finalizeHeaders();
        BooleanSupplier finisher = getFinisher();
        return new RestFuture<>(this, shouldQueue, finisher, data, rawData, route, headers);
    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException
    {
        if (CallbackContext.isCallbackContext())
            throw new IllegalStateException("Preventing use of complete() in callback threads! This operation can be a deadlock cause");
        try
        {
            return submit(shouldQueue).get();
        }
        catch (Throwable e)
        {
            if (e instanceof ExecutionException)
            {
                Throwable t = e.getCause();
                if (t instanceof RateLimitedException)
                    throw (RateLimitedException) t;
                else if (t instanceof PermissionException)
                    throw (PermissionException) t;
                else if (t instanceof ErrorResponseException)
                    throw (ErrorResponseException) t;
            }
            throw new RuntimeException(e);
        }
    }

    protected RequestBody finalizeData() { return data; }
    protected Route.CompiledRoute finalizeRoute() { return route; }
    protected CaseInsensitiveMap<String, String> finalizeHeaders() { return null; }
    protected BooleanSupplier finalizeChecks() { return null; }

    protected RequestBody getRequestBody(DataObject object)
    {
        this.rawData = object;

        return object == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, object.toString());
    }

    protected RequestBody getRequestBody(DataArray array)
    {
        this.rawData = array;

        return array == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, array.toString());
    }

    private CheckWrapper getFinisher()
    {
        BooleanSupplier pre = finalizeChecks();
        BooleanSupplier wrapped = this.checks;
        return (pre != null || wrapped != null) ? new CheckWrapper(wrapped, pre) : CheckWrapper.EMPTY;
    }

    public void handleResponse(Response response, Request<T> request)
    {
        if (response.isOk())
            handleSuccess(response, request);
        else
            request.onFailure(response);
    }

    protected void handleSuccess(Response response, Request<T> request)
    {
        if (handler == null)
            request.onSuccess(null);
        else
            request.onSuccess(handler.apply(response, request));
    }

    /*
        useful for final permission checks:

        @Override
        protected BooleanSupplier finalizeChecks()
        {
            // throw exception, if missing perms
            return () -> hasPermission(Permission.MESSAGE_WRITE);
        }
     */
    protected static class CheckWrapper implements BooleanSupplier
    {
        public static final CheckWrapper EMPTY = new CheckWrapper(null, null)
        {
            public boolean getAsBoolean() { return true; }
        };

        protected final BooleanSupplier pre;
        protected final BooleanSupplier wrapped;

        public CheckWrapper(BooleanSupplier wrapped, BooleanSupplier pre)
        {
            this.pre = pre;
            this.wrapped = wrapped;
        }

        public boolean pre()
        {
            return pre == null || pre.getAsBoolean();
        }

        public boolean test()
        {
            return wrapped == null || wrapped.getAsBoolean();
        }

        @Override
        public boolean getAsBoolean()
        {
            return pre() && test();
        }
    }
}
