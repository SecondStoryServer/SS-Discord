
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.PrivateChannel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Indicates that a Message contains {@link MessageEmbed Embeds} in a {@link PrivateChannel PrivateChannel}.
 * 
 * <p>Can be used to retrieve affected PrivateChannel, the ID of the deleted Message and a list of MessageEmbeds.
 */
public class PrivateMessageEmbedEvent extends GenericPrivateMessageEvent
{
    private final List<MessageEmbed> embeds;

    public PrivateMessageEmbedEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull PrivateChannel channel, @Nonnull List<MessageEmbed> embeds)
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
