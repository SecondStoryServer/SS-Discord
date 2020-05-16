package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateSystemChannelEvent extends GenericGuildUpdateEvent<TextChannel> {
    public static final String IDENTIFIER = "system_channel";

    public GuildUpdateSystemChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable TextChannel oldSystemChannel) {
        super(api, responseNumber, guild, oldSystemChannel, guild.getSystemChannel(), IDENTIFIER);
    }


    @Nullable
    public TextChannel getOldSystemChannel() {
        return getOldValue();
    }


    @Nullable
    public TextChannel getNewSystemChannel() {
        return getNewValue();
    }
}
