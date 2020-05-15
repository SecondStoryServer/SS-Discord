
package net.dv8tion.jda.api.events.channel.priv;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.PrivateChannel Private Channel} was deleted.
 *
 * <p>Can be used to retrieve the issuing {@link net.dv8tion.jda.api.entities.User User}.
 */
public class PrivateChannelDeleteEvent extends Event
{
    protected final PrivateChannel channel;

    public PrivateChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull PrivateChannel channel)
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
