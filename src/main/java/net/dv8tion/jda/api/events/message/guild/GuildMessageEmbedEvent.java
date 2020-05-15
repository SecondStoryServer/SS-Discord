
package net.dv8tion.jda.api.events.message.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Indicates that a Guild Message contains one or more {@link net.dv8tion.jda.api.entities.MessageEmbed Embeds}.
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
     * The {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbeds}
     *
     * @return The MessageEmbeds
     */
    @Nonnull
    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}
