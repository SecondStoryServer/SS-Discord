package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateVerificationLevelEvent extends GenericGuildUpdateEvent<Guild.VerificationLevel> {
    public static final String IDENTIFIER = "verification_level";

    public GuildUpdateVerificationLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.VerificationLevel oldVerificationLevel) {
        super(api, responseNumber, guild, oldVerificationLevel, guild.getVerificationLevel(), IDENTIFIER);
    }


    @Nonnull
    public Guild.VerificationLevel getOldVerificationLevel() {
        return getOldValue();
    }


    @Nonnull
    public Guild.VerificationLevel getNewVerificationLevel() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.VerificationLevel getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.VerificationLevel getNewValue() {
        return super.getNewValue();
    }
}
