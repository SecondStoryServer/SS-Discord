package me.syari.ss.discord.internal.utils.config.flags;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public enum ConfigFlag {
    RAW_EVENTS(false),
    USE_RELATIVE_RATELIMIT(true),
    RETRY_TIMEOUT(true),
    BULK_DELETE_SPLIT(true),
    SHUTDOWN_HOOK(true),
    MDC_CONTEXT(true),
    AUTO_RECONNECT(true),
    GUILD_SUBSCRIPTIONS(true);

    private final boolean isDefault;

    ConfigFlag(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public static @NotNull EnumSet<ConfigFlag> getDefault() {
        EnumSet<ConfigFlag> set = EnumSet.noneOf(ConfigFlag.class);
        for (ConfigFlag flag : values()) {
            if (flag.isDefault)
                set.add(flag);
        }
        return set;
    }
}
