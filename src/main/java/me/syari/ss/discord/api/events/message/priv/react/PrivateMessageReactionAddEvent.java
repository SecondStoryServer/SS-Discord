

package me.syari.ss.discord.api.events.message.priv.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link MessageReaction MessageReaction} was added to a Message in a PrivateChannel.
 *
 * <p>Can be used to detect when a reaction is added in a private channel.
 */
public class PrivateMessageReactionAddEvent extends GenericPrivateMessageReactionEvent
{
    public PrivateMessageReactionAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, reaction, userId);
    }
}
