package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Mentionable;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class Role implements Mentionable, Comparable<Role> {
    private final long id;
    private final SnowflakeReference<Guild> guild;

    private String name;

    public Role(long id, Guild guild) {
        this.id = id;
        JDAImpl api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getAsMention() {
        return "<@&" + getId() + '>';
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Role))
            return false;
        Role oRole = (Role) o;
        return this.getIdLong() == oRole.getIdLong();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "R:" + getName() + '(' + id + ')';
    }

    @Override
    public int compareTo(@NotNull Role role) {
        if (this == role)
            return 0;

        if (this.guild.getIdLong() != role.guild.getIdLong())
            throw new IllegalArgumentException("Cannot compare roles that aren't from the same guild!");

        OffsetDateTime thisTime = this.getTimeCreated();
        OffsetDateTime rTime = role.getTimeCreated();

        return rTime.compareTo(thisTime);
    }

    // -- Setters --

    public void setName(String name) {
        this.name = name;
    }
}
