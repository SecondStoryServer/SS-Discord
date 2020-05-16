

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateSplashEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "splash";

    public GuildUpdateSplashEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldSplashId)
    {
        super(api, responseNumber, guild, oldSplashId, guild.getSplashId(), IDENTIFIER);
    }


    @Nullable
    public String getOldSplashId()
    {
        return getOldValue();
    }


    @Nullable
    public String getOldSplashUrl()
    {
        return previous == null ? null : String.format(Guild.SPLASH_URL, guild.getId(), previous);
    }


    @Nullable
    public String getNewSplashId()
    {
        return getNewValue();
    }


    @Nullable
    public String getNewSplashUrl()
    {
        return next == null ? null : String.format(Guild.SPLASH_URL, guild.getId(), next);
    }
}
