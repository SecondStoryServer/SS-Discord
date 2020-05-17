package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface SnowflakeCacheView<T extends ISnowflake> extends CacheView<T> {

    @Nullable
    T getElementById(long id);


    @Nullable
    default T getElementById(@NotNull String id) {
        return getElementById(MiscUtil.parseSnowflake(id));
    }
}
