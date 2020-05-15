
package net.dv8tion.jda.api.events.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Indicates that a Message contains an {@link net.dv8tion.jda.api.entities.MessageEmbed Embed} in a {@link net.dv8tion.jda.api.entities.MessageChannel MessageChannel}.
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
     * The list of {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbeds}
     *
     * @return The list of MessageEmbeds
     */
    @Nonnull
    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}
