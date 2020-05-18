package me.syari.ss.discord.internal.utils.config;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaConfig {
    private final ConcurrentMap<String, String> mdcContextMap;
    private final int maxBufferSize;

    public MetaConfig(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
        this.mdcContextMap = new ConcurrentHashMap<>();
    }

    @Nullable
    public ConcurrentMap<String, String> getMdcContextMap() {
        return mdcContextMap;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

}
