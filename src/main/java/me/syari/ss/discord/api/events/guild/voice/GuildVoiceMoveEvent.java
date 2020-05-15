

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} moves between {@link VoiceChannel VoiceChannels}.
 *
 * <p><b>When the {@link Member Member} is leaving a {@link GuildVoiceLeaveEvent GuildVoiceLeaveEvent} is fired instead</b>
 *
 * <p>Can be used to detect when a member moves from one voice channel to another in the same guild.
 *
 * @see GuildVoiceUpdateEvent GuildVoiceUpdateEvent
 */
public class GuildVoiceMoveEvent extends GenericGuildVoiceUpdateEvent
{
    public GuildVoiceMoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull VoiceChannel channelLeft)
    {
        super(api, responseNumber, member, channelLeft, member.getVoiceState().getChannel());
    }

    @Nonnull
    @Override
    public VoiceChannel getChannelLeft()
    {
        return super.getChannelLeft();
    }

    @Nonnull
    @Override
    public VoiceChannel getChannelJoined()
    {
        return super.getChannelJoined();
    }

    @Nonnull
    @Override
    public VoiceChannel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public VoiceChannel getNewValue()
    {
        return super.getNewValue();
    }
}
