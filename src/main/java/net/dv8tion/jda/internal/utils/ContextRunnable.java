

package net.dv8tion.jda.internal.utils;

import net.dv8tion.jda.api.audit.ThreadLocalReason;

import java.util.concurrent.Callable;

public class ContextRunnable<E> implements Runnable, Callable<E>
{
    private final String localReason;
    private final Runnable runnable;
    private final Callable<E> callable;

    public ContextRunnable(Runnable runnable)
    {
        this.localReason = ThreadLocalReason.getCurrent();
        this.runnable = runnable;
        this.callable = null;
    }

    public ContextRunnable(Callable<E> callable)
    {
        this.localReason = ThreadLocalReason.getCurrent();
        this.runnable = null;
        this.callable = callable;
    }

    @Override
    public void run()
    {
        try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason))
        {
            runnable.run();
        }
    }

    @Override
    public E call() throws Exception
    {
        try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason))
        {
            return callable.call();
        }
    }
}
