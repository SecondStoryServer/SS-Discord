package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;

public class SnowflakeCacheViewImpl<T extends ISnowflake> extends AbstractCacheView<T> implements SnowflakeCacheView<T> {
    public SnowflakeCacheViewImpl(Class<T> type) {
        super(type);
    }

    @Override
    public T getElementById(long id) {
        if (elements.isEmpty()) {
            return null;
        }
        return get(id);
    }
}
