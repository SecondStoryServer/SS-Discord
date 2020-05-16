package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildChannel;

import javax.annotation.Nonnull;


public interface ChannelOrderAction extends OrderAction<GuildChannel, ChannelOrderAction> {

    @Nonnull
    Guild getGuild();


}
