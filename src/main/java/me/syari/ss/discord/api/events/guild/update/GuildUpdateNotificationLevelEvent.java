

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateNotificationLevelEvent extends GenericGuildUpdateEvent<Guild.NotificationLevel>
{
    public static final String IDENTIFIER = "notification_level";

    public GuildUpdateNotificationLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.NotificationLevel oldNotificationLevel)
    {
        super(api, responseNumber, guild, oldNotificationLevel, guild.getDefaultNotificationLevel(), IDENTIFIER);
    }


    @Nonnull
    public Guild.NotificationLevel getOldNotificationLevel()
    {
        return getOldValue();
    }


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
