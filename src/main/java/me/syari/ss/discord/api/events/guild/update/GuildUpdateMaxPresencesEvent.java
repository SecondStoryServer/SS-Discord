package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateMaxPresencesEvent extends GenericGuildUpdateEvent<Integer> {
    public static final String IDENTIFIER = "max_presences";

    public GuildUpdateMaxPresencesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous) {
        super(api, responseNumber, guild, previous, guild.getMaxPresences(), IDENTIFIER);
    }


    public int getOldMaxPresences() {
        return getOldValue();
    }


    public int getNewMaxPresences() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Integer getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Integer getNewValue() {
        return super.getNewValue();
    }
}
