

package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.sharding.DefaultShardManagerBuilder;


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
