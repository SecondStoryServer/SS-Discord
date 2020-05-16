package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface GuildVoiceState {

    @Nonnull
    JDA getJDA();


    boolean isSelfMuted();


    boolean isSelfDeafened();


    boolean isMuted();


    boolean isDeafened();


    boolean isGuildMuted();


    boolean isGuildDeafened();


    boolean isSuppressed();

    @Nonnull
    Guild getGuild();


    @Nonnull
    Member getMember();


}
