
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;

import javax.annotation.Nonnull;

/**
 * Represents a Discord Voice GuildChannel.
 * <br>Adds additional information specific to voice channels in Discord.
 *
 * @see GuildChannel
 * @see TextChannel
 * @see Category
 *
 * @see   Guild#getVoiceChannelCache()
 * @see   Guild#getVoiceChannels()
 * @see   Guild#getVoiceChannelsByName(String, boolean)
 * @see   Guild#getVoiceChannelById(long)
 *
 * @see   JDA#getVoiceChannelCache()
 * @see   JDA#getVoiceChannels()
 * @see   JDA#getVoiceChannelsByName(String, boolean)
 * @see   JDA#getVoiceChannelById(long)
 */
public interface VoiceChannel extends GuildChannel
{
    /**
     * The maximum amount of {@link Member Members} that can be in this
     * {@link VoiceChannel VoiceChannel} at once.
     * <br>0 - No limit
     *
     * @return The maximum amount of members allowed in this channel at once.
     */
    int getUserLimit();

    /**
     * The audio bitrate of the voice audio that is transmitted in this channel. While higher bitrates can be sent to
     * this channel, it will be scaled down by the client.
     * <br>Default and recommended value is 64000
     *
     * @return The audio bitrate of this voice channel.
     */
    int getBitrate();

    @Nonnull
    @Override
    ChannelAction<VoiceChannel> createCopy(@Nonnull Guild guild);

    @Nonnull
    @Override
    ChannelAction<VoiceChannel> createCopy();
}
