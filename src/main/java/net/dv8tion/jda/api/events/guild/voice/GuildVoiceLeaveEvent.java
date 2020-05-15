

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} disconnected from a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}.
 *
 * <p><b>When the {@link net.dv8tion.jda.api.entities.Member Member} is moved a {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent GuildVoiceMoveEvent} is fired instead</b>
 *
 * <p>Can be used to detect when a member leaves a voice channel completely.
 *
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent GuildVoiceUpdateEvent
 */
public class GuildVoiceLeaveEvent extends GenericGuildVoiceUpdateEvent
{
    public GuildVoiceLeaveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull VoiceChannel channelLeft)
    {
        super(api, responseNumber, member, channelLeft, null);
    }

    @Nonnull
    @Override
    public VoiceChannel getChannelLeft()
    {
        return super.getChannelLeft();
    }

    @Nonnull
    @Override
    public VoiceChannel getOldValue()
    {
        return super.getOldValue();
    }
}
