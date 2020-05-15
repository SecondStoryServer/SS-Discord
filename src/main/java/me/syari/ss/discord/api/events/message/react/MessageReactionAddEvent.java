

package me.syari.ss.discord.api.events.message.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

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
