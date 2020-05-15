
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link TextChannel TextChannel}'s NSFW status changed.
 *
 * <p>Can be used to detect when a TextChannel NSFW status changes and get its previous value.
 *
 * <p>Identifier: {@code nsfw}
 */
public class TextChannelUpdateNSFWEvent extends GenericTextChannelUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "nsfw";

    public TextChannelUpdateNSFWEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, boolean oldNsfw)
    {
        super(api, responseNumber, channel, oldNsfw, channel.isNSFW(), IDENTIFIER);
    }

    /**
     * Whether the channel was marked NSFW before
     *
     * @return True, if the channel was marked NSFW before
     */
    public boolean getOldNSFW()
    {
        return getOldValue();
    }
}
