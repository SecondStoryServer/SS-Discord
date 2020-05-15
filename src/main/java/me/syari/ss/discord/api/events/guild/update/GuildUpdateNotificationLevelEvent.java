

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link Guild.NotificationLevel NotificationLevel} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when a NotificationLevel changes and retrieve the old one
 *
 * <p>Identifier: {@code notification_level}
 */
public class GuildUpdateNotificationLevelEvent extends GenericGuildUpdateEvent<Guild.NotificationLevel>
{
    public static final String IDENTIFIER = "notification_level";

    public GuildUpdateNotificationLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.NotificationLevel oldNotificationLevel)
    {
        super(api, responseNumber, guild, oldNotificationLevel, guild.getDefaultNotificationLevel(), IDENTIFIER);
    }

    /**
     * The old {@link Guild.NotificationLevel NotificationLevel}
     *
     * @return The old NotificationLevel
     */
    @Nonnull
    public Guild.NotificationLevel getOldNotificationLevel()
    {
        return getOldValue();
    }

    /**
     * The new {@link Guild.NotificationLevel NotificationLevel}
     *
     * @return The new NotificationLevel
     */
    @Nonnull
    public Guild.NotificationLevel getNewNotificationLevel()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.NotificationLevel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.NotificationLevel getNewValue()
    {
        return super.getNewValue();
    }
}
