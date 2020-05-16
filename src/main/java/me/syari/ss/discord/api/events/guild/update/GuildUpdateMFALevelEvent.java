

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateMFALevelEvent extends GenericGuildUpdateEvent<Guild.MFALevel>
{
    public static final String IDENTIFIER = "mfa_level";

    public GuildUpdateMFALevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.MFALevel oldMFALevel)
    {
        super(api, responseNumber, guild, oldMFALevel, guild.getRequiredMFALevel(), IDENTIFIER);
    }


    @Nonnull
    public Guild.MFALevel getOldMFALevel()
    {
        return getOldValue();
    }


    @Nonnull
    public Guild.MFALevel getNewMFALevel()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.MFALevel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.MFALevel getNewValue()
    {
        return super.getNewValue();
    }
}
