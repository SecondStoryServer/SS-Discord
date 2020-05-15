

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateExplicitContentLevelEvent extends GenericGuildUpdateEvent<Guild.ExplicitContentLevel>
{
    public static final String IDENTIFIER = "explicit_content_filter";

    public GuildUpdateExplicitContentLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.ExplicitContentLevel oldLevel)
    {
        super(api, responseNumber, guild, oldLevel, guild.getExplicitContentLevel(), IDENTIFIER);
    }


    @Nonnull
    public Guild.ExplicitContentLevel getOldLevel()
    {
        return getOldValue();
    }


    @Nonnull
    public Guild.ExplicitContentLevel getNewLevel()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.ExplicitContentLevel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.ExplicitContentLevel getNewValue()
    {
        return super.getNewValue();
    }
}
