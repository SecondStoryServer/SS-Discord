

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.ApplicationInfo;

public class ApplicationInfoImpl implements ApplicationInfo
{
    private final long id;

    public ApplicationInfoImpl(long id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(final Object obj)
    {
        return obj instanceof ApplicationInfoImpl && this.id == ((ApplicationInfoImpl) obj).id;
    }

    @Override
    public long getIdLong()
    {
        return this.id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(this.id);
    }

    @Override
    public String toString()
    {
        return "ApplicationInfo(" + this.id + ")";
    }

}
