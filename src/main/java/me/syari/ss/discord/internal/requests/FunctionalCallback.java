

package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.utils.IOBiConsumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.BiConsumer;

public class FunctionalCallback implements Callback
{
    private final BiConsumer<Call, IOException> failure;
    private final IOBiConsumer<Call, Response> success;

    public FunctionalCallback(BiConsumer<Call, IOException> failure, IOBiConsumer<Call, Response> success)
    {
        this.failure = failure;
        this.success = success;
    }

    public static Builder onSuccess(IOBiConsumer<Call, Response> callback)
    {
        return new Builder().onSuccess(callback);
    }

    public static Builder onFailure(BiConsumer<Call, IOException> callback)
    {
        return new Builder().onFailure(callback);
    }

    @Override
    public void onFailure(@Nonnull Call call, @Nonnull IOException e)
    {
        if (failure != null)
            failure.accept(call, e);
    }

    @Override
    public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException
    {
        if (success != null)
            success.accept(call, response);
    }

    public static class Builder
    {
        private BiConsumer<Call, IOException> failure;
        private IOBiConsumer<Call, Response> success;

        public Builder onSuccess(IOBiConsumer<Call, Response> callback)
        {
            this.success = callback;
            return this;
        }

        public Builder onFailure(BiConsumer<Call, IOException> callback)
        {
            this.failure = callback;
            return this;
        }

        public FunctionalCallback build()
        {
            return new FunctionalCallback(failure, success);
        }
    }
}
