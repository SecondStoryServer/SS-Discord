
package me.syari.ss.discord.api.events.message.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a Guild Message was deleted.
 * 
 * <p>Can be used retrieve affected TextChannel and the id of the deleted Message.
 */
public class GuildMessageDeleteEvent extends GenericGuildMessageEvent
{
    public GuildMessageDeleteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
