
package me.syari.ss.discord.api.events.message.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.util.List;


public class GuildMessageEmbedEvent extends GenericGuildMessageEvent
{
    private final List<MessageEmbed> embeds;

    public GuildMessageEmbedEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel, @Nonnull List<MessageEmbed> embeds)
    {
        super(api, responseNumber, messageId, channel);
        this.embeds = embeds;
    }

    
    @Nonnull
    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}
