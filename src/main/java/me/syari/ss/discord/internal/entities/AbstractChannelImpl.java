package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;

public abstract class AbstractChannelImpl<T extends GuildChannel, M extends AbstractChannelImpl<T, M>> implements GuildChannel {
    protected final long id;
    protected final SnowflakeReference<Guild> guild;
    protected final JDAImpl api;

    protected String name;
    protected int rawPosition;

    public AbstractChannelImpl(long id, GuildImpl guild) {
        this.id = id;
        this.api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    @Override
    public int compareTo(@Nonnull GuildChannel o) {
        Checks.notNull(o, "Channel");
        if (getPositionRaw() != o.getPositionRaw())
            return Integer.compare(getPositionRaw(), o.getPositionRaw());
        return Long.compareUnsigned(id, o.getIdLong());
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public GuildImpl getGuild() {
        return (GuildImpl) guild.resolve();
    }

    @Override
    public int getPositionRaw() {
        return rawPosition;
    }


    @Override
    public long getIdLong() {
        return id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GuildChannel))
            return false;
        GuildChannel channel = (GuildChannel) obj;
        return channel.getIdLong() == getIdLong();
    }

    @SuppressWarnings("unchecked")
    public M setName(String name) {
        this.name = name;
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M setPosition(int rawPosition) {
        this.rawPosition = rawPosition;
        return (M) this;
    }
}
