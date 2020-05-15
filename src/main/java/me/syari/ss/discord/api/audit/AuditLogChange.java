

package me.syari.ss.discord.api.audit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public class AuditLogChange
{
    protected final Object oldValue;
    protected final Object newValue;
    protected final String key;

    public AuditLogChange(Object oldValue, Object newValue, String key)
    {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.key = key;
    }


    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getOldValue()
    {
        return (T) oldValue;
    }


    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getNewValue()
    {
        return (T) newValue;
    }


    @Nonnull
    public String getKey()
    {
        return key;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, oldValue, newValue);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AuditLogChange))
            return false;
        AuditLogChange other = (AuditLogChange) obj;
        return other.key.equals(key)
                && Objects.equals(other.oldValue, oldValue)
                && Objects.equals(other.newValue, newValue);
    }

    @Override
    public String toString()
    {
        return String.format("ALC:%s(%s -> %s)", key, oldValue, newValue);
    }
}
