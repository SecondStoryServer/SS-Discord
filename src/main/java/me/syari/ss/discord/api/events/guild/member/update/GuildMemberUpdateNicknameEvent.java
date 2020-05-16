package me.syari.ss.discord.api.events.guild.member.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildMemberUpdateNicknameEvent extends GenericGuildMemberUpdateEvent<String> {
    public static final String IDENTIFIER = "nick";

    public GuildMemberUpdateNicknameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable String oldNick) {
        super(api, responseNumber, member, oldNick, member.getNickname(), IDENTIFIER);
    }


    @Nullable
    public String getOldNickname() {
        return getOldValue();
    }


    @Nullable
    public String getNewNickname() {
        return getNewValue();
    }
}
