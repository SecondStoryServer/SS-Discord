package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractChannelImpl<T extends AbstractChannelImpl<T>> implements GuildChannel {
    protected final long id;
    protected final SnowflakeReference<Guild> guild;
    protected final JDAImpl api;

    protected String name;

    public AbstractChannelImpl(long id, @NotNull Guild guild) {
        this.id = id;
        this.api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    @Override
    public int compareTo(@NotNull GuildChannel o) {
        return Long.compareUnsigned(id, o.getIdLong());
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return guild.resolve();
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

    public void setName(String name) {
        this.name = name;
    }
}
