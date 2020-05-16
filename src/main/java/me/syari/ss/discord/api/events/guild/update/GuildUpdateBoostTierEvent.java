package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateBoostTierEvent extends GenericGuildUpdateEvent<Guild.BoostTier> {
    public static final String IDENTIFIER = "boost_tier";

    public GuildUpdateBoostTierEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.BoostTier previous) {
        super(api, responseNumber, guild, previous, guild.getBoostTier(), IDENTIFIER);
    }


    @Nonnull
    public Guild.BoostTier getOldBoostTier() {
        return getOldValue();
    }


    @Nonnull
    public Guild.BoostTier getNewBoostTier() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.BoostTier getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.BoostTier getNewValue() {
        return super.getNewValue();
    }
}
