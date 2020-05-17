package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Mentionable;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Set;

public class Emote implements Mentionable {
    private final long id;
    private final Set<Role> roles;

    private boolean animated = false;
    private String name;

    public Emote(long id) {
        this.id = id;
        this.roles = null;
    }

    @Nonnull
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

    @Nonnull
    @Override
    public String getAsMention() {
        return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
    }

    // -- Setters --

    public Emote setName(String name) {
        this.name = name;
        return this;
    }

    public Emote setAnimated(boolean animated) {
        this.animated = animated;
        return this;
    }

    // -- Set Getter --

    public Set<Role> getRoleSet() {
        return this.roles;
    }

    // -- Object overrides --

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Emote))
            return false;

        Emote oEmote = (Emote) obj;
        return this.id == oEmote.id && getName().equals(oEmote.getName());
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
