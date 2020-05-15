

package net.dv8tion.jda.api.utils;

/**
 * Compression algorithms that can be used with JDA.
 *
 * @see net.dv8tion.jda.api.JDABuilder#setCompression(Compression)
 * @see net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder#setCompression(Compression)
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
