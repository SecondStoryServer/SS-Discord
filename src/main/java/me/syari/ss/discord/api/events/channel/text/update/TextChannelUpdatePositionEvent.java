
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;


public class TextChannelUpdatePositionEvent extends GenericTextChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    public TextChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, int oldPosition)
    {
        super(api, responseNumber, channel, oldPosition, channel.getPositionRaw(), IDENTIFIER);
    }


    public int getOldPosition()
    {
        return getOldValue();
    }


    public int getNewPosition()
    {
        return getNewValue();
    }
}
