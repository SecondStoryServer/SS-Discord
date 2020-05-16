package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateBannerEvent extends GenericGuildUpdateEvent<String> {
    public static final String IDENTIFIER = "banner";

    public GuildUpdateBannerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous) {
        super(api, responseNumber, guild, previous, guild.getBannerId(), IDENTIFIER);
    }


    @Nullable
    public String getNewBannerId() {
        return getNewValue();
    }


    @Nullable
    public String getNewBannerIdUrl() {
        return next == null ? null : String.format(Guild.BANNER_URL, guild.getId(), next);
    }


    @Nullable
    public String getOldBannerId() {
        return getOldValue();
    }


    @Nullable
    public String getOldBannerUrl() {
        return previous == null ? null : String.format(Guild.BANNER_URL, guild.getId(), previous);
    }
}
