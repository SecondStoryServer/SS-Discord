

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateIconEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "icon";

    public GuildUpdateIconEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldIconId)
    {
        super(api, responseNumber, guild, oldIconId, guild.getIconId(), IDENTIFIER);
    }


    @Nullable
    public String getOldIconId()
    {
        return getOldValue();
    }


    @Nullable
    public String getOldIconUrl()
    {
        return previous == null ? null : String.format(Guild.ICON_URL, guild.getId(), previous, previous.startsWith("a_") ? "gif" : "png");
    }


    @Nullable
    public String getNewIconId()
    {
        return getNewValue();
    }


    @Nullable
    public String getNewIconUrl()
    {
        return next == null ? null : String.format(Guild.ICON_URL, guild.getId(), next, next.startsWith("a_") ? "gif" : "png");
    }
}
