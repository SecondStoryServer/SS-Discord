package me.syari.ss.discord.internal.utils.config;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaConfig {
    private final ConcurrentMap<String, String> mdcContextMap = new ConcurrentHashMap<>();
    private final int maxBufferSize;

    public MetaConfig(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    @NotNull
    public ConcurrentMap<String, String> getMdcContextMap() {
        return mdcContextMap;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

}
