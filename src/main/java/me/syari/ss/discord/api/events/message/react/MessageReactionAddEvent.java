

package me.syari.ss.discord.api.events.message.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MessageReactionAddEvent extends GenericMessageReactionEvent
{
    public MessageReactionAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user,
                                   @Nullable Member member, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, member, reaction, userId);
    }
}
