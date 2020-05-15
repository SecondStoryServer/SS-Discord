
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link TextChannel TextChannel}'s name changed.
 *
 * <p>Can be used to detect when a TextChannel name changes and get its previous name.
 *
 * <p>Identifier: {@code name}
 */
public class TextChannelUpdateNameEvent extends GenericTextChannelUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public TextChannelUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nonnull String oldName)
    {
        super(api, responseNumber, channel, oldName, channel.getName(), IDENTIFIER);
    }

    /**
     * The old name
     *
     * @return The old name
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name
     *
     * @return The new name
     */
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
