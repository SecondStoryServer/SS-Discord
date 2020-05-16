

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateVanityCodeEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "vanity_code";

    public GuildUpdateVanityCodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
    {
        super(api, responseNumber, guild, previous, guild.getVanityCode(), IDENTIFIER);
    }

    
    @Nullable
    public String getOldVanityCode()
    {
        return getOldValue();
    }


    @Nullable
    public String getOldVanityUrl()
    {
        return getOldVanityCode() == null ? null : "https://discord.gg/" + getOldVanityCode();
    }


    @Nullable
    public String getNewVanityCode()
    {
        return getNewValue();
    }


    @Nullable
    public String getNewVanityUrl()
    {
        return getNewVanityCode() == null ? null : "https://discord.gg/" + getNewVanityCode();
    }
}
