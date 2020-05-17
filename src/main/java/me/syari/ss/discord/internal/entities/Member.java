package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Mentionable;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Member implements Mentionable {
    private final SnowflakeReference<Guild> guild;
    private final User user;
    private final Set<Role> roles = ConcurrentHashMap.newKeySet();

    private String nickname;
    private long boostDate;

    public Member(Guild guild, User user) {
        JDAImpl api = user.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
        this.user = user;
    }

    @Nonnull
    public User getUser() {
        return user;
    }

    @Nonnull
    private Guild getGuild() {
        return guild.resolve();
    }

    public String getNickname() {
        return nickname;
    }

    @Nonnull
    public String getDisplayName() {
        return nickname != null ? nickname : getUser().getName();
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
        if (!(o instanceof Member))
            return false;

        Member oMember = (Member) o;
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
