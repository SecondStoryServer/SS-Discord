

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.UpdateEvent;

import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} joined or left a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}.
 * <br>Generic event that combines
 * {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent GuildVoiceLeaveEvent},
 * {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent GuildVoiceJoinEvent}, and
 * {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent GuildVoiceMoveEvent} for convenience.
 *
 * <p>Can be used to detect when a Member leaves/joins a channel
 *
 * <p>Identifier: {@code voice-channel}
 */
public interface GuildVoiceUpdateEvent extends UpdateEvent<Member, VoiceChannel>
{
    String IDENTIFIER = "voice-channel";

    /**
     * The {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} that the {@link net.dv8tion.jda.api.entities.Member Member} is moved from
     *
     * @return The {@link net.dv8tion.jda.api.entities.VoiceChannel}
     */
    @Nullable
    VoiceChannel getChannelLeft();

    /**
     * The {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} that was joined
     *
     * @return The {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}
     */
    @Nullable
    VoiceChannel getChannelJoined();
}
