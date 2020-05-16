package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MemberImpl implements Member {
    private final SnowflakeReference<Guild> guild;
    private final User user;
    private final JDAImpl api;
    private final Set<Role> roles = ConcurrentHashMap.newKeySet();

    private String nickname;
    private long boostDate;

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

    public void setBoostDate(long boostDate) {
        this.boostDate = boostDate;
    }

    public Set<Role> getRoleSet() {
        return roles;
    }

    public long getBoostDateRaw() {
        return boostDate;
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
