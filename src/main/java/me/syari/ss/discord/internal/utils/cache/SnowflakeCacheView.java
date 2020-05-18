package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView;

public class SnowflakeCacheView<T extends ISnowflake> extends AbstractCacheView<T> implements ISnowflakeCacheView<T> {
    public SnowflakeCacheView(Class<T> type) {
        super(type);
    }

    @Override
    public T getElementById(long id) {
        return elements.isEmpty() ? null : get(id);
    }
}
