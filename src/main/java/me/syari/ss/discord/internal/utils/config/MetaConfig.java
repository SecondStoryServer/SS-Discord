package me.syari.ss.discord.internal.utils.config;

import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaConfig {
    private final ConcurrentMap<String, String> mdcContextMap;
    private final boolean useShutdownHook;
    private final boolean guildSubscriptions;
    private final int maxBufferSize;

    public MetaConfig(int maxBufferSize, @NotNull EnumSet<ConfigFlag> flags) {
        this.maxBufferSize = maxBufferSize;
        boolean enableMDC = flags.contains(ConfigFlag.MDC_CONTEXT);
        if (enableMDC) {
            this.mdcContextMap = new ConcurrentHashMap<>();
        } else {
            this.mdcContextMap = null;
        }
        this.useShutdownHook = flags.contains(ConfigFlag.SHUTDOWN_HOOK);
        this.guildSubscriptions = flags.contains(ConfigFlag.GUILD_SUBSCRIPTIONS);
    }

    @Nullable
    public ConcurrentMap<String, String> getMdcContextMap() {
        return mdcContextMap;
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

}
