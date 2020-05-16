

package me.syari.ss.discord.api.audit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class ThreadLocalReason
{
    private static ThreadLocal<String> currentReason;

    private ThreadLocalReason()
    {
        throw new UnsupportedOperationException();
    }


    public static void setCurrent(@Nullable String reason)
    {
        if (reason != null)
        {
            if (currentReason == null)
                currentReason = new ThreadLocal<>();
            currentReason.set(reason);
        }
        else if (currentReason != null)
        {
            currentReason.remove();
        }
    }


    @Nullable
    public static String getCurrent()
    {
        return currentReason == null ? null : currentReason.get();
    }


    @Nonnull
    public static Closable closable(@Nullable String reason)
    {
        return new ThreadLocalReason.Closable(reason);
    }


    public static class Closable implements AutoCloseable
    {
        private final String previous;

        public Closable(@Nullable String reason)
        {
            this.previous = getCurrent();
            setCurrent(reason);
        }

        @Override
        public void close()
        {
            setCurrent(previous);
        }
    }
}
