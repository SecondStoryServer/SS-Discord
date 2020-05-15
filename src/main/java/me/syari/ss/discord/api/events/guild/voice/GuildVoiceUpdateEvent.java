

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nullable;

/**
 * Indicates that a {@link Member Member} joined or left a {@link VoiceChannel VoiceChannel}.
 * <br>Generic event that combines
 * {@link GuildVoiceLeaveEvent GuildVoiceLeaveEvent},
 * {@link GuildVoiceJoinEvent GuildVoiceJoinEvent}, and
 * {@link GuildVoiceMoveEvent GuildVoiceMoveEvent} for convenience.
 *
 * <p>Can be used to detect when a Member leaves/joins a channel
 *
 * <p>Identifier: {@code voice-channel}
 */
public interface GuildVoiceUpdateEvent extends UpdateEvent<Member, VoiceChannel>
{
    String IDENTIFIER = "voice-channel";

    /**
     * The {@link VoiceChannel VoiceChannel} that the {@link Member Member} is moved from
     *
     * @return The {@link VoiceChannel}
     */
    @Nullable
    VoiceChannel getChannelLeft();

    /**
     * The {@link VoiceChannel VoiceChannel} that was joined
     *
     * @return The {@link VoiceChannel VoiceChannel}
     */
    @Nullable
    VoiceChannel getChannelJoined();
}
