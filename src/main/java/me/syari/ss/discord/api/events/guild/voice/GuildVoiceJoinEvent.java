

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} connected to a {@link VoiceChannel VoiceChannel}.
 *
 * <p><b>When the {@link Member Member} is moved a {@link GuildVoiceMoveEvent GuildVoiceMoveEvent} is fired instead</b>
 *
 * <p>Can be used to detect when a member joins a voice channel for the first time.
 */
public class GuildVoiceJoinEvent extends GenericGuildVoiceUpdateEvent
{
    public GuildVoiceJoinEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member, null, member.getVoiceState().getChannel());
    }

    @Nonnull
    @Override
    public VoiceChannel getChannelJoined()
    {
        return super.getChannelJoined();
    }

    @Nonnull
    @Override
    public VoiceChannel getNewValue()
    {
        return super.getNewValue();
    }
}
