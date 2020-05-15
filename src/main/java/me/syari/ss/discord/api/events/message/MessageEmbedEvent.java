
package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Indicates that a Message contains an {@link MessageEmbed Embed} in a {@link MessageChannel MessageChannel}.
 * <br>Discord may need to do additional calculations and resizing tasks on messages that embed websites, thus they send the message only with content and link and use this update to add the missing embed later when the server finishes those calculations.
 * 
 * <p>Can be used to retrieve MessageEmbeds from any message. No matter if private or guild.
 */
public class MessageEmbedEvent extends GenericMessageEvent
{
    private final List<MessageEmbed> embeds;

    public MessageEmbedEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel, @Nonnull List<MessageEmbed> embeds)
    {
        super(api, responseNumber, messageId, channel);
        this.embeds = Collections.unmodifiableList(embeds);
    }

    /**
     * The list of {@link MessageEmbed MessageEmbeds}
     *
     * @return The list of MessageEmbeds
     */
    @Nonnull
    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}
