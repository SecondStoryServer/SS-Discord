

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.ApplicationInfo;

import javax.annotation.Nonnull;

public class ApplicationInfoImpl implements ApplicationInfo
{
    private final JDA api;


    private final long id;
    private final String name;

    public ApplicationInfoImpl(JDA api, long id, String name)
    {
        this.api = api;
        this.id = id;
        this.name = name;
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

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return this.api;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return this.name;
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
