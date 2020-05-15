

package me.syari.ss.discord.api.events.message.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

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
