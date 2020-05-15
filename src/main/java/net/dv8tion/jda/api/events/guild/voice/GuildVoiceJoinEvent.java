

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} connected to a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}.
 *
 * <p><b>When the {@link net.dv8tion.jda.api.entities.Member Member} is moved a {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent GuildVoiceMoveEvent} is fired instead</b>
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
