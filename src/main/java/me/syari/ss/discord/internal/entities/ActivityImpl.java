package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.RichPresence;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ActivityImpl implements Activity {
    protected final String name;
    protected final String url;
    protected final ActivityType type;
    protected final Timestamps timestamps;

    protected ActivityImpl(String name, String url, ActivityType type, RichPresence.Timestamps timestamps) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.timestamps = timestamps;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Nonnull
    @Override
    public ActivityType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        return Objects.hash(name, type, url, timestamps);
    }

    @Override
    public String toString() {
        if (url != null)
            return String.format("Activity(%s | %s)", name, url);
        else
            return String.format("Activity(%s)", name);
    }
}
