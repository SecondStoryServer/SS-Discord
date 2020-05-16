package me.syari.ss.discord.internal.utils.config.sharding;

import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentMap;
import java.util.function.IntFunction;

public class ShardingMetaConfig extends MetaConfig {
    private static final ShardingMetaConfig defaultConfig = new ShardingMetaConfig(2048, null, null, ConfigFlag.getDefault(), Compression.ZLIB);
    private final Compression compression;
    private final IntFunction<? extends ConcurrentMap<String, String>> contextProvider;

    public ShardingMetaConfig(
            int maxBufferSize,
            @Nullable IntFunction<? extends ConcurrentMap<String, String>> contextProvider,
            @Nullable EnumSet<CacheFlag> cacheFlags, EnumSet<ConfigFlag> flags, Compression compression) {
        super(maxBufferSize, null, cacheFlags, flags);

        this.compression = compression;
        this.contextProvider = contextProvider;
    }

    @Nullable
    public ConcurrentMap<String, String> getContextMap(int shardId) {
        return contextProvider == null ? null : contextProvider.apply(shardId);
    }

    public Compression getCompression() {
        return compression;
    }

    @Nullable
    public IntFunction<? extends ConcurrentMap<String, String>> getContextProvider() {
        return contextProvider;
    }

    @Nonnull
    public static ShardingMetaConfig getDefault() {
        return defaultConfig;
    }
}
