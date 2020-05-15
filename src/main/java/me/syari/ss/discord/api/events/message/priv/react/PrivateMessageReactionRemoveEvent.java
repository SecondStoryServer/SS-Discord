

package me.syari.ss.discord.api.events.message.priv.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link MessageReaction MessageReaction} was removed from a Message in a PrivateChannel.
 *
 * <p>Can be used to detect when a reaction is removed in a private channel.
 */
public class PrivateMessageReactionRemoveEvent extends GenericPrivateMessageReactionEvent
{
    public PrivateMessageReactionRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, reaction, userId);
    }
}
