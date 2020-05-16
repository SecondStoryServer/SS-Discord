package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemberImpl implements Member {
    private static final ZoneOffset OFFSET = ZoneOffset.of("+00:00");
    private final SnowflakeReference<Guild> guild;
    private final User user;
    private final JDAImpl api;
    private final Set<Role> roles = ConcurrentHashMap.newKeySet();

    private String nickname;
    private long joinDate, boostDate;

    public MemberImpl(GuildImpl guild, User user) {
        this.api = (JDAImpl) user.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
        this.user = user;
    }

    @Nonnull
    @Override
    public User getUser() {
        return user;
    }

    @Nonnull
    @Override
    public GuildImpl getGuild() {
        return (GuildImpl) guild.resolve();
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return api;
    }

    @Nonnull
    @Override
    public OffsetDateTime getTimeJoined() {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(joinDate), OFFSET);
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return nickname != null ? nickname : getUser().getName();
    }

    @Override
    public boolean isOwner() {
        return this.user.getIdLong() == getGuild().getOwnerIdLong();
    }

    @Override
    public boolean isFake() {
        return getGuild().getMemberById(getIdLong()) == null;
    }

    @Override
    public long getIdLong() {
        return user.getIdLong();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public MemberImpl setJoinDate(long joinDate) {
        this.joinDate = joinDate;
        return this;
    }

    public void setBoostDate(long boostDate) {
        this.boostDate = boostDate;
    }

    public Set<Role> getRoleSet() {
        return roles;
    }

    public long getBoostDateRaw() {
        return boostDate;
    }

    public boolean isIncomplete() {
        // the joined_at is only present on complete members, this implies the member is completely loaded
        return !isOwner() && Objects.equals(getGuild().getTimeCreated(), getTimeJoined());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MemberImpl))
            return false;

        MemberImpl oMember = (MemberImpl) o;
        return oMember.user.getIdLong() == user.getIdLong()
                && oMember.guild.getIdLong() == guild.getIdLong();
    }

    @Override
    public int hashCode() {
        return (guild.getIdLong() + user.getId()).hashCode();
    }

    @Override
    public String toString() {
        return "MB:" + getDisplayName() + '(' + getUser().toString() + " / " + getGuild().toString() + ')';
    }

    @Nonnull
    @Override
    public String getAsMention() {
        return (nickname == null ? "<@" : "<@!") + user.getId() + '>';
    }

}
