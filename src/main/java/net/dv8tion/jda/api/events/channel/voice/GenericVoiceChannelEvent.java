
package net.dv8tion.jda.api.events.channel.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} event was fired.
 * <br>Every VoiceChannelEvent is derived from this event and can be casted.
 *
 * <p>Can be used to detect any VoiceChannelEvent.
 */
public abstract class GenericVoiceChannelEvent extends Event
{
    private final VoiceChannel channel;

    public GenericVoiceChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel)
    {
        super(api, responseNumber);
        this.channel = channel;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}
     *
     * @return The VoiceChannel
     */
    @Nonnull
    public VoiceChannel getChannel()
    {
        return channel;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.Guild Guild}
     * <br>Shortcut for {@code getChannel().getGuild()}
     *
     * @return The Guild
     */
    @Nonnull
    public Guild getGuild()
    {
        return channel.getGuild();
    }
}
