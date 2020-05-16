package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateAfkTimeoutEvent extends GenericGuildUpdateEvent<Guild.Timeout> {
    public static final String IDENTIFIER = "afk_timeout";

    public GuildUpdateAfkTimeoutEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.Timeout oldAfkTimeout) {
        super(api, responseNumber, guild, oldAfkTimeout, guild.getAfkTimeout(), IDENTIFIER);
    }


    @Nonnull
    public Guild.Timeout getOldAfkTimeout() {
        return getOldValue();
    }


    @Nonnull
    public Guild.Timeout getNewAfkTimeout() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.Timeout getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.Timeout getNewValue() {
        return super.getNewValue();
    }
}
