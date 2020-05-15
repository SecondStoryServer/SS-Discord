
package net.dv8tion.jda.api.events.channel.priv;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.PrivateChannel Private Channel} was created.
 *
 * <p>Can be used to retrieve the created private channel and its {@link net.dv8tion.jda.api.entities.User User}.
 */
public class PrivateChannelCreateEvent extends Event
{
    private final PrivateChannel channel;

    public PrivateChannelCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull PrivateChannel channel)
    {
        super(api, responseNumber);
        this.channel = channel;
    }

    /**
     * The target {@link net.dv8tion.jda.api.entities.User User}
     * <br>Shortcut for {@code getPrivateChannel().getUser()}
     *
     * @return The User
     */
    @Nonnull
    public User getUser()
    {
        return channel.getUser();
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.PrivateChannel PrivateChannel}
     *
     * @return The PrivateChannel
     */
    @Nonnull
    public PrivateChannel getChannel()
    {
        return channel;
    }
}
