

package net.dv8tion.jda.internal.utils.cache;

import net.dv8tion.jda.api.entities.ISnowflake;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.function.LongFunction;

public class SnowflakeReference<T extends ISnowflake> implements ISnowflake
{
    private final LongFunction<T> fallbackProvider;
    private final long id;

    //We intentionally use a WeakReference rather than a SoftReference:
    // The reasoning is that we want to replace an old reference as soon as possible with a more up-to-date instance.
    // A soft reference would not be released until the user stops using it (ideally) so that is the wrong reference to use.
    private WeakReference<T> reference;

    public SnowflakeReference(T referent, LongFunction<T> fallback)
    {
        this.fallbackProvider = fallback;
        this.reference = new WeakReference<>(referent);
        this.id = referent.getIdLong();
    }

    @Nonnull
    public T resolve()
    {
        T referent = reference.get();
        if (referent == null)
        {
            referent = fallbackProvider.apply(id);
            if (referent == null)
                throw new IllegalStateException("Cannot get reference as it has already been Garbage Collected");
            reference = new WeakReference<>(referent);
        }
        return referent;
    }

    @Override
    public int hashCode()
    {
        return resolve().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return resolve().equals(obj);
    }

    @Override
    public String toString()
    {
        return resolve().toString();
    }

    @Override
    public long getIdLong()
    {
        return id;
    }
}
