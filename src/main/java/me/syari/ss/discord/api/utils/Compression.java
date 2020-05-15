

package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.sharding.DefaultShardManagerBuilder;

/**
 * Compression algorithms that can be used with JDA.
 *
 * @see JDABuilder#setCompression(Compression)
 * @see DefaultShardManagerBuilder#setCompression(Compression)
 */
public enum Compression
{
    /** Don't use any compression */
    NONE(""),
    /** Use ZLIB transport compression */
    ZLIB("zlib-stream");

    private final String key;

    Compression(String key)
    {
        this.key = key;
    }

    /**
     * The key used for the gateway query to enable this compression
     *
     * @return The query key
     */
    public String getKey()
    {
        return key;
    }
}
