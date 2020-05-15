
package net.dv8tion.jda.api.events.channel.text;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} event was fired.
 * <br>Every TextChannelEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any TextChannelEvent.
 */
public abstract class GenericTextChannelEvent extends Event
{
    private final TextChannel channel;

    public GenericTextChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel)
    {
        super(api, responseNumber);
        this.channel = channel;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}
     *
     * @return The TextChannel
     */
    @Nonnull
    public TextChannel getChannel()
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
