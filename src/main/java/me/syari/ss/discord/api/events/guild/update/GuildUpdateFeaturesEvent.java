package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import java.util.Set;


public class GuildUpdateFeaturesEvent extends GenericGuildUpdateEvent<Set<String>> {
    public static final String IDENTIFIER = "features";

    public GuildUpdateFeaturesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Set<String> oldFeatures) {
        super(api, responseNumber, guild, oldFeatures, guild.getFeatures(), IDENTIFIER);
    }


    @Nonnull
    public Set<String> getOldFeatures() {
        return getOldValue();
    }


    @Nonnull
    public Set<String> getNewFeatures() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Set<String> getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Set<String> getNewValue() {
        return super.getNewValue();
    }
}
