package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Member implements ISnowflake {
    private final SnowflakeReference<Guild> guild;
    private final User user;
    private final Set<Role> roles = ConcurrentHashMap.newKeySet();

    private String nickname;
    private long boostDate;

    public Member(Guild guild, @NotNull User user) {
        JDA api = user.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
        this.user = user;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    @NotNull
    private Guild getGuild() {
        return guild.resolve();
    }

    public String getNickname() {
        return nickname;
    }

    @NotNull
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
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Member)) {
            return false;
        }

        Member member = (Member) object;
        return member.user.getIdLong() == user.getIdLong() && member.guild.getIdLong() == guild.getIdLong();
    }

    @Override
    public int hashCode() {
        return (guild.getIdLong() + user.getId()).hashCode();
    }

    @Override
    public String toString() {
        return "MB:" + getDisplayName() + '(' + getUser().toString() + " / " + getGuild().toString() + ')';
    }

}
