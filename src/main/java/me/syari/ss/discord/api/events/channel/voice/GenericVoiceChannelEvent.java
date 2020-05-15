
package me.syari.ss.discord.api.events.channel.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel} event was fired.
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
     * The {@link VoiceChannel VoiceChannel}
     *
     * @return The VoiceChannel
     */
    @Nonnull
    public VoiceChannel getChannel()
    {
        return channel;
    }

    /**
     * The {@link Guild Guild}
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
