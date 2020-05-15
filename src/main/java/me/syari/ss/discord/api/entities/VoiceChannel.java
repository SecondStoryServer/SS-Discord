
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.requests.restaction.ChannelAction;

import javax.annotation.Nonnull;


public interface VoiceChannel extends GuildChannel
{

    int getUserLimit();


    int getBitrate();

    @Nonnull
    @Override
    ChannelAction<VoiceChannel> createCopy(@Nonnull Guild guild);

    @Nonnull
    @Override
    ChannelAction<VoiceChannel> createCopy();
}
