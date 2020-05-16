package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.ListedEmote;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class EmoteImpl implements ListedEmote {
    private final long id;
    private final Set<Role> roles;

    private boolean animated = false;
    private String name;

    public EmoteImpl(long id, boolean fake) {
        this.id = id;
        this.roles = ConcurrentHashMap.newKeySet();
    }

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

    public EmoteImpl setUser(User user) {
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
