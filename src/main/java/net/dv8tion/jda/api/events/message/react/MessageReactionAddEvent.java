

package net.dv8tion.jda.api.events.message.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a user added a reaction to a message
 * <br>This includes unicode and custom emoji
 *
 * <p>Can be used to track when a user adds a reaction to a message
 */
public class MessageReactionAddEvent extends GenericMessageReactionEvent
{
    public MessageReactionAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user,
                                   @Nullable Member member, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, member, reaction, userId);
    }
}
