
package me.syari.ss.discord.api.events.message.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Indicates that a Guild Message contains one or more {@link MessageEmbed Embeds}.
 * 
 * <p>Can be used to retrieve the affected TextChannel, the id of the affected Message and a list of MessageEmbeds.
 */
public class GuildMessageEmbedEvent extends GenericGuildMessageEvent
{
    private final List<MessageEmbed> embeds;

    public GuildMessageEmbedEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel, @Nonnull List<MessageEmbed> embeds)
    {
        super(api, responseNumber, messageId, channel);
        this.embeds = embeds;
    }

    /**
     * The {@link MessageEmbed MessageEmbeds}
     *
     * @return The MessageEmbeds
     */
    @Nonnull
    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}
