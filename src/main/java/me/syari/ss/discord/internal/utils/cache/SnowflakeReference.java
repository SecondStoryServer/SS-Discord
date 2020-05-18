package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.function.LongFunction;

public class SnowflakeReference<T extends ISnowflake> implements ISnowflake {
    private final LongFunction<T> fallbackProvider;
    private final long id;

    private WeakReference<T> reference;

    public SnowflakeReference(T referent, LongFunction<T> fallback) {
        this.fallbackProvider = fallback;
        this.reference = new WeakReference<>(referent);
        this.id = referent.getIdLong();
    }

    @NotNull
    public T resolve() {
        T referent = reference.get();
        if (referent == null) {
            referent = fallbackProvider.apply(id);
            if (referent == null)
                throw new IllegalStateException("Cannot get reference as it has already been Garbage Collected");
            reference = new WeakReference<>(referent);
        }
        return referent;
    }

    @Override
    public int hashCode() {
        return resolve().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return resolve().equals(object);
    }

    @Override
    public String toString() {
        return resolve().toString();
    }

    @Override
    public long getIdLong() {
        return id;
    }
}
