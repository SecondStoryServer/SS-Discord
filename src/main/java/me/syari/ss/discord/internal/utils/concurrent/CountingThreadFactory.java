package me.syari.ss.discord.internal.utils.concurrent;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class CountingThreadFactory implements ThreadFactory {
    private final Supplier<String> identifier;
    private final AtomicLong count = new AtomicLong(1);

    public CountingThreadFactory(@NotNull Supplier<String> identifier, @NotNull String specifier) {
        this.identifier = () -> identifier.get() + " " + specifier;
    }

    @NotNull
    @Override
    public Thread newThread(@NotNull Runnable r) {
        final Thread thread = new Thread(r, identifier.get() + "-Worker " + count.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}
