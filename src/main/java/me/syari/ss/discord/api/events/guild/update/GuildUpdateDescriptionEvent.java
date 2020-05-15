

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateDescriptionEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "description";

    public GuildUpdateDescriptionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
    {
        super(api, responseNumber, guild, previous, guild.getDescription(), IDENTIFIER);
    }


    @Nullable
    public String getOldDescription()
    {
        return getOldValue();
    }


    @Nullable
    public String getNewDescription()
    {
        return getNewValue();
    }
}
