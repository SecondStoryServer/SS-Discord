

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the system channel of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 * <br>This is used for welcome messages
 *
 * <p>Can be used to detect when a guild system channel changes and retrieve the old one
 *
 * <p>Identifier: {@code system_channel}
 */
public class GuildUpdateSystemChannelEvent extends GenericGuildUpdateEvent<TextChannel>
{
    public static final String IDENTIFIER = "system_channel";

    public GuildUpdateSystemChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable TextChannel oldSystemChannel)
    {
        super(api, responseNumber, guild, oldSystemChannel, guild.getSystemChannel(), IDENTIFIER);
    }

    /**
     * The previous system channel.
     * 
     * @return The previous system channel
     */
    @Nullable
    public TextChannel getOldSystemChannel()
    {
        return getOldValue();
    }

    /**
     * The new system channel.
     *
     * @return The new system channel
     */
    @Nullable
    public TextChannel getNewSystemChannel()
    {
        return getNewValue();
    }
}
