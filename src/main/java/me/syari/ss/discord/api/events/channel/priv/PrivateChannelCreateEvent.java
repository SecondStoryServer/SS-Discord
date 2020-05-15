
package me.syari.ss.discord.api.events.channel.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link PrivateChannel Private Channel} was created.
 *
 * <p>Can be used to retrieve the created private channel and its {@link User User}.
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
     * The target {@link User User}
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
     * The {@link PrivateChannel PrivateChannel}
     *
     * @return The PrivateChannel
     */
    @Nonnull
    public PrivateChannel getChannel()
    {
        return channel;
    }
}
