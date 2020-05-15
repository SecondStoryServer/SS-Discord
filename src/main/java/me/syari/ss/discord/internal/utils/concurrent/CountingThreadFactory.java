

package me.syari.ss.discord.internal.utils.concurrent;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class CountingThreadFactory implements ThreadFactory
{
    private final Supplier<String> identifier;
    private final AtomicLong count = new AtomicLong(1);

    public CountingThreadFactory(@Nonnull Supplier<String> identifier, @Nonnull String specifier)
    {
        this.identifier = () -> identifier.get() + " " + specifier;
    }

    @Nonnull
    @Override
    public Thread newThread(@Nonnull Runnable r)
    {
        final Thread thread = new Thread(r, identifier.get() + "-Worker " + count.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}
