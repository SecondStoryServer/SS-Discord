
package net.dv8tion.jda.api.events.channel.voice.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel}'s name changed.
 *
 * <p>Can be used to get affected VoiceChannel, affected Guild and previous name.
 *
 * <p>Identifier: {@code name}
 */
public class VoiceChannelUpdateNameEvent extends GenericVoiceChannelUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public VoiceChannelUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, @Nonnull String oldName)
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
