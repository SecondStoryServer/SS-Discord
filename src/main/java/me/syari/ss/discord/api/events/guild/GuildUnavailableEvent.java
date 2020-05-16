package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUnavailableEvent extends GenericGuildEvent {
    public GuildUnavailableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild) {
        super(api, responseNumber, guild);
    }
}
