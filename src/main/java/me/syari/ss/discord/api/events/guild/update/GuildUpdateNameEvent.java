package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateNameEvent extends GenericGuildUpdateEvent<String> {
    public static final String IDENTIFIER = "name";

    public GuildUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull String oldName) {
        super(api, responseNumber, guild, oldName, guild.getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName() {
        return getOldValue();
    }


    @Nonnull
    public String getNewName() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue() {
        return super.getNewValue();
    }
}
