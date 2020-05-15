
package net.dv8tion.jda.api.events.message.priv;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Indicates that a Message contains {@link net.dv8tion.jda.api.entities.MessageEmbed Embeds} in a {@link net.dv8tion.jda.api.entities.PrivateChannel PrivateChannel}.
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
