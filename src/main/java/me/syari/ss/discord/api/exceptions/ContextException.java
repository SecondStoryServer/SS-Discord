package me.syari.ss.discord.api.exceptions;

import javax.annotation.Nonnull;
import java.util.function.Consumer;


public class ContextException extends Exception {


    @Nonnull
    public static Consumer<Throwable> here(@Nonnull Consumer<? super Throwable> acceptor) {
        return new ContextConsumer(new ContextException(), acceptor);
    }

    public static class ContextConsumer implements Consumer<Throwable> {
        private final ContextException context;
        private final Consumer<? super Throwable> callback;

        private ContextConsumer(ContextException context, Consumer<? super Throwable> callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        public void accept(Throwable throwable) {
            Throwable cause = throwable;
            while (cause.getCause() != null)
                cause = cause.getCause();
            cause.initCause(context);
            if (callback != null)
                callback.accept(throwable);
        }
    }
}
