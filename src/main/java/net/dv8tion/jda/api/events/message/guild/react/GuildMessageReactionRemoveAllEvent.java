

package net.dv8tion.jda.api.events.message.guild.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;

import javax.annotation.Nonnull;

/**
 * Indicates that the reactions for a {@link net.dv8tion.jda.api.entities.Message Message} were cleared by a moderator in a guild.
 *
 * <p>Can be used to detect when the reaction of a message are cleared by a moderator.
 */
public class GuildMessageReactionRemoveAllEvent extends GenericGuildMessageEvent
{
    public GuildMessageReactionRemoveAllEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
