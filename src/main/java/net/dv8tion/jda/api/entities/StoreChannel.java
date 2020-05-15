

package net.dv8tion.jda.api.entities;

import net.dv8tion.jda.api.JDA;

/**
 * Represents a Discord Store GuildChannel.
 *
 * @since  4.0.0
 *
 * @see   Guild#getStoreChannelCache()
 * @see   Guild#getStoreChannels()
 * @see   Guild#getStoreChannelsByName(String, boolean)
 * @see   Guild#getStoreChannelById(long)
 *
 * @see   JDA#getStoreChannelCache()
 * @see   JDA#getStoreChannels()
 * @see   JDA#getStoreChannelsByName(String, boolean)
 * @see   JDA#getStoreChannelById(long)
 */
public interface StoreChannel extends GuildChannel {}
