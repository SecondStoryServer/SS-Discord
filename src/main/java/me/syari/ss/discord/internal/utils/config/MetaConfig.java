package me.syari.ss.discord.internal.utils.config;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaConfig {
    private final ConcurrentMap<String, String> mdcContextMap = new ConcurrentHashMap<>();

    public MetaConfig() {
    }

    @NotNull
    public ConcurrentMap<String, String> getMdcContextMap() {
        return mdcContextMap;
    }

}
