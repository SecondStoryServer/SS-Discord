package me.syari.ss.discord.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ThreadLocalReason {
    private static ThreadLocal<String> currentReason;

    private ThreadLocalReason() {
        throw new UnsupportedOperationException();
    }

    public static void setCurrent(@Nullable String reason) {
        if (reason != null) {
            if (currentReason == null)
                currentReason = new ThreadLocal<>();
            currentReason.set(reason);
        } else if (currentReason != null) {
            currentReason.remove();
        }
    }

    @Nullable
    public static String getCurrent() {
        return currentReason == null ? null : currentReason.get();
    }

    @NotNull
    public static Closable closable(@Nullable String reason) {
        return new Closable(reason);
    }

    public static class Closable implements AutoCloseable {
        private final String previous;

        public Closable(@Nullable String reason) {
            this.previous = getCurrent();
            setCurrent(reason);
        }

        @Override
        public void close() {
            setCurrent(previous);
        }
    }
}
