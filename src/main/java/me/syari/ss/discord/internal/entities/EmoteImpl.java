package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.ListedEmote;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.managers.EmoteManager;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class EmoteImpl implements ListedEmote {
    private final long id;
    private final SnowflakeReference<Guild> guild;
    private final JDAImpl api;
    private final Set<Role> roles;
    private final boolean fake;

    private final ReentrantLock mngLock = new ReentrantLock();
    private volatile EmoteManager manager = null;

    private boolean managed = false;
    private boolean animated = false;
    private String name;
    private User user;

    public EmoteImpl(long id, GuildImpl guild) {
        this(id, guild, false);
    }

    public EmoteImpl(long id, GuildImpl guild, boolean fake) {
        this.id = id;
        this.api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
        this.roles = ConcurrentHashMap.newKeySet();
        this.fake = fake;
    }

    public EmoteImpl(long id, JDAImpl api) {
        this.id = id;
        this.api = api;
        this.guild = null;
        this.roles = null;
        this.fake = true;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFake() {
        return fake;
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @Nonnull
    @Override
    public User getUser() {
        if (!hasUser())
            throw new IllegalStateException("This emote does not have a user");
        return user;
    }

    @Override
    public boolean hasUser() {
        return user != null;
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

    public EmoteImpl setManaged(boolean val) {
        this.managed = val;
        return this;
    }

    public EmoteImpl setUser(User user) {
        this.user = user;
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
