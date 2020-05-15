
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;


public class TextChannelUpdateNSFWEvent extends GenericTextChannelUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "nsfw";

    public TextChannelUpdateNSFWEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, boolean oldNsfw)
    {
        super(api, responseNumber, channel, oldNsfw, channel.isNSFW(), IDENTIFIER);
    }


    public boolean getOldNSFW()
    {
        return getOldValue();
    }
}
