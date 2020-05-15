

package net.dv8tion.jda.api.events.message.guild.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.MessageReaction MessageReaction} was removed from a Message in a Guild
 *
 * <p>Can be used to detect when a reaction is removed in a guild
 */
public class GuildMessageReactionRemoveEvent extends GenericGuildMessageReactionEvent
{
    public GuildMessageReactionRemoveEvent(@Nonnull JDA api, long responseNumber, @Nullable Member member, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, member, reaction, userId);
    }
}
