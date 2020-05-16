package me.syari.ss.discord.internal.utils.config;

import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaConfig {
    private static final MetaConfig defaultConfig = new MetaConfig(2048, null, EnumSet.allOf(CacheFlag.class), ConfigFlag.getDefault());
    private final ConcurrentMap<String, String> mdcContextMap;
    private final EnumSet<CacheFlag> cacheFlags;
    private final boolean enableMDC;
    private final boolean useShutdownHook;
    private final boolean guildSubscriptions;
    private final int maxBufferSize;

    public MetaConfig(
            int maxBufferSize,
            @Nullable ConcurrentMap<String, String> mdcContextMap,
            @Nullable EnumSet<CacheFlag> cacheFlags, EnumSet<ConfigFlag> flags) {
        this.maxBufferSize = maxBufferSize;
        this.cacheFlags = cacheFlags == null ? EnumSet.allOf(CacheFlag.class) : cacheFlags;
        this.enableMDC = flags.contains(ConfigFlag.MDC_CONTEXT);
        if (enableMDC)
            this.mdcContextMap = mdcContextMap == null ? new ConcurrentHashMap<>() : null;
        else
            this.mdcContextMap = null;
        this.useShutdownHook = flags.contains(ConfigFlag.SHUTDOWN_HOOK);
        this.guildSubscriptions = flags.contains(ConfigFlag.GUILD_SUBSCRIPTIONS);
    }

    @Nullable
    public ConcurrentMap<String, String> getMdcContextMap() {
        return mdcContextMap;
    }

    @Nonnull
    public EnumSet<CacheFlag> getCacheFlags() {
        return cacheFlags;
    }

    public boolean isEnableMDC() {
        return enableMDC;
    }

    public boolean isUseShutdownHook() {
        return useShutdownHook;
    }

    public boolean isGuildSubscriptions() {
        return guildSubscriptions;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    @Nonnull
    public static MetaConfig getDefault() {
        return defaultConfig;
    }
}
