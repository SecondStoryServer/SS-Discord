package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.ISnowflake;
import org.jetbrains.annotations.Nullable;

public interface ISnowflakeCacheView<T extends ISnowflake> extends CacheView<T> {
    @Nullable
    T getElementById(long id);
}
