
package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.RichPresence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ActivityImpl implements Activity
{
    protected final String name;
    protected final String url;
    protected final ActivityType type;
    protected final Timestamps timestamps;
    protected final Emoji emoji;

    protected ActivityImpl(String name)
    {
        this(name, null, ActivityType.DEFAULT);
    }

    protected ActivityImpl(String name, String url)
    {
        this(name, url, ActivityType.STREAMING);
    }

    protected ActivityImpl(String name, String url, ActivityType type)
    {
        this(name, url, type, null, null);
    }

    protected ActivityImpl(String name, String url, ActivityType type, RichPresence.Timestamps timestamps, Emoji emoji)
    {
        this.name = name;
        this.url = url;
        this.type = type;
        this.timestamps = timestamps;
        this.emoji = emoji;
    }

    @Override
    public boolean isRich()
    {
        return false;
    }

    @Override
    public RichPresence asRichPresence()
    {
        return null;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    @Nonnull
    @Override
    public ActivityType getType()
    {
        return type;
    }

    @Nullable
    public RichPresence.Timestamps getTimestamps()
    {
        return timestamps;
    }

    @Nullable
    @Override
    public Emoji getEmoji()
    {
        return emoji;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof ActivityImpl))
            return false;

        ActivityImpl oGame = (ActivityImpl) o;
        return oGame.getType() == type
               && Objects.equals(name, oGame.getName())
               && Objects.equals(url, oGame.getUrl())
               && Objects.equals(timestamps, oGame.timestamps);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, type, url, timestamps);
    }

    @Override
    public String toString()
    {
        if (url != null)
            return String.format("Activity(%s | %s)", name, url);
        else
            return String.format("Activity(%s)", name);
    }
}
