

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface GuildVoiceState
{

    @Nonnull
    JDA getJDA();


    boolean isSelfMuted();


    boolean isSelfDeafened();


    boolean isMuted();


    boolean isDeafened();


    boolean isGuildMuted();


    boolean isGuildDeafened();


    boolean isSuppressed();


    @Nullable
    VoiceChannel getChannel();


    @Nonnull
    Guild getGuild();


    @Nonnull
    Member getMember();


    boolean inVoiceChannel();


    @Nullable
    String getSessionId();
}
