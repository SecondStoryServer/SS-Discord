

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} moves between {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannels}.
 *
 * <p><b>When the {@link net.dv8tion.jda.api.entities.Member Member} is leaving a {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent GuildVoiceLeaveEvent} is fired instead</b>
 *
 * <p>Can be used to detect when a member moves from one voice channel to another in the same guild.
 *
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent GuildVoiceUpdateEvent
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
