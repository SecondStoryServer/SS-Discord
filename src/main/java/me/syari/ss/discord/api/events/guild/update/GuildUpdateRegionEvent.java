package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateRegionEvent extends GenericGuildUpdateEvent<Region> {
    public static final String IDENTIFIER = "region";

    private final String oldRegion;
    private final String newRegion;

    public GuildUpdateRegionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull String oldRegion) {
        super(api, responseNumber, guild, Region.fromKey(oldRegion), guild.getRegion(), IDENTIFIER);
        this.oldRegion = oldRegion;
        this.newRegion = guild.getRegionRaw();
    }


    @Nonnull
    public Region getOldRegion() {
        return getOldValue();
    }


    @Nonnull
    public String getOldRegionRaw() {
        return oldRegion;
    }


    @Nonnull
    public Region getNewRegion() {
        return getNewValue();
    }


    @Nonnull
    public String getNewRegionRaw() {
        return newRegion;
    }

    @Nonnull
    @Override
    public Region getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Region getNewValue() {
        return super.getNewValue();
    }
}
