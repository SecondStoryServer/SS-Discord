

package net.dv8tion.jda.api.events.message.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a user removed the reaction on a message
 * 
 * <p>Can be used to detect when a reaction is removed from a message
 */
public class MessageReactionRemoveEvent extends GenericMessageReactionEvent
{
    public MessageReactionRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user,
                                      @Nullable Member member, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, member, reaction, userId);
    }
}
