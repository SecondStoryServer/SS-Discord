package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import org.jetbrains.annotations.NotNull;

public class Emote implements ISnowflake {
    private final long id;
    private boolean animated = false;
    private String name = "";

    public Emote(long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public long getIdLong() {
        return id;
    }

    public boolean isAnimated() {
        return animated;
    }

    @NotNull
    public String getAsMention() {
        return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof Emote)) return false;
        Emote emote = (Emote) object;
        return this.id == emote.id && getName().equals(emote.getName());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "E:" + getName() + '(' + getIdLong() + ')';
    }
}
