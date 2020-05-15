

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the afk-channel of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when an afk channel changes and retrieve the old one
 *
 * <p>Identifier: {@code afk_channel}
 */
public class GuildUpdateAfkChannelEvent extends GenericGuildUpdateEvent<VoiceChannel>
{
    public static final String IDENTIFIER = "afk_channel";

    public GuildUpdateAfkChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable VoiceChannel oldAfkChannel)
    {
        super(api, responseNumber, guild, oldAfkChannel, guild.getAfkChannel(), IDENTIFIER);
    }

    /**
     * The old afk channel
     *
     * @return The old afk channel, or null
     */
    @Nullable
    public VoiceChannel getOldAfkChannel()
    {
        return getOldValue();
    }

    /**
     * The new afk channel
     *
     * @return The new afk channel, or null
     */
    @Nullable
    public VoiceChannel getNewAfkChannel()
    {
        return getNewValue();
    }
}
