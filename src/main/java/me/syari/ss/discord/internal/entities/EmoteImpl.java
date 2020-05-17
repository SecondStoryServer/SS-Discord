package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Set;

public class EmoteImpl implements Emote {
    private final long id;
    private final Set<Role> roles;

    private boolean animated = false;
    private String name;

    public EmoteImpl(long id) {
        this.id = id;
        this.roles = null;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @Override
    public boolean isAnimated() {
        return animated;
    }

    // -- Setters --

    public EmoteImpl setName(String name) {
        this.name = name;
        return this;
    }

    public EmoteImpl setAnimated(boolean animated) {
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
        if (!(obj instanceof EmoteImpl))
            return false;

        EmoteImpl oEmote = (EmoteImpl) obj;
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
