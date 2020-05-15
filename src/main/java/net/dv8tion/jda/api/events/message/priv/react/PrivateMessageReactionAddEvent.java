

package net.dv8tion.jda.api.events.message.priv.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.MessageReaction MessageReaction} was added to a Message in a PrivateChannel.
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
