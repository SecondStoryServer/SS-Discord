package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class Role implements ISnowflake {
    private final long id;
    private final SnowflakeReference<Guild> guild;

    private String name;

    public Role(long id, @NotNull Guild guild) {
        this.id = id;
        JDAImpl api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getAsMention() {
        return "<@&" + getId() + '>';
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Role)) {
            return false;
        }
        Role role = (Role) object;
        return this.getIdLong() == role.getIdLong();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "R:" + getName() + '(' + id + ')';
    }

    public void setName(String name) {
        this.name = name;
    }
}
