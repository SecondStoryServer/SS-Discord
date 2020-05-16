package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;

public class RoleImpl implements Role {
    private final long id;
    private final SnowflakeReference<Guild> guild;

    private String name;

    public RoleImpl(long id, Guild guild) {
        this.id = id;
        JDAImpl api = (JDAImpl) guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
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
    public int compareTo(@Nonnull Role r) {
        if (this == r)
            return 0;
        if (!(r instanceof RoleImpl))
            throw new IllegalArgumentException("Cannot compare different role implementations");
        RoleImpl impl = (RoleImpl) r;

        if (this.guild.getIdLong() != impl.guild.getIdLong())
            throw new IllegalArgumentException("Cannot compare roles that aren't from the same guild!");

        OffsetDateTime thisTime = this.getTimeCreated();
        OffsetDateTime rTime = r.getTimeCreated();

        //We compare the provided role's time to this's time instead of the reverse as one would expect due to how
        // discord deals with hierarchy. The more recent a role was created, the lower its hierarchy ranking when
        // it shares the same position as another role.
        return rTime.compareTo(thisTime);
    }

    // -- Setters --

    public void setName(String name) {
        this.name = name;
    }
}
