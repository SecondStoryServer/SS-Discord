
package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class MessageEmbedEvent extends GenericMessageEvent
{
    private final List<MessageEmbed> embeds;

    public MessageEmbedEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel, @Nonnull List<MessageEmbed> embeds)
    {
        super(api, responseNumber, messageId, channel);
        this.embeds = Collections.unmodifiableList(embeds);
    }


    @Nonnull
    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}
