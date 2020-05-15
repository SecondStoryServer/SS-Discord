

package me.syari.ss.discord.api.events.message.guild.react;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.events.message.guild.GenericGuildMessageEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that the reactions for a {@link Message Message} were cleared by a moderator in a guild.
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
