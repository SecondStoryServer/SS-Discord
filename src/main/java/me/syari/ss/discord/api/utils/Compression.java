

package me.syari.ss.discord.api.utils;

public enum Compression
{

    NONE(""),

    ZLIB("zlib-stream");

    private final String key;

    Compression(String key)
    {
        this.key = key;
    }

    
    public String getKey()
    {
        return key;
    }
}
