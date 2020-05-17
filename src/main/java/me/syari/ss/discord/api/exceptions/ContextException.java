package me.syari.ss.discord.api.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ContextException extends Exception {
    @NotNull
    public static Consumer<Throwable> here(@NotNull Consumer<? super Throwable> acceptor) {
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
