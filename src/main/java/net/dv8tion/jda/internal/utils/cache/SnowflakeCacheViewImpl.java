

package net.dv8tion.jda.internal.utils.cache;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import java.util.function.Function;

public class SnowflakeCacheViewImpl<T extends ISnowflake> extends AbstractCacheView<T> implements SnowflakeCacheView<T>
{
    public SnowflakeCacheViewImpl(Class<T> type, Function<T, String> nameMapper)
    {
        super(type, nameMapper);
    }

    @Override
    public T getElementById(long id)
    {
        if (elements.isEmpty())
            return null;
        return get(id);
    }
}
