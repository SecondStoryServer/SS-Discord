

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link CacheView CacheView} implementation
 * specifically to view {@link ISnowflake ISnowflake} implementations.
 *
 * @see CacheView CacheView for details on Efficient Memory Usage
 */
public interface SnowflakeCacheView<T extends ISnowflake> extends CacheView<T>
{
    /**
     * Retrieves the entity represented by the provided ID.
     *
     * @param  id
     *         The ID of the entity
     *
     * @return Possibly-null entity for the specified ID
     */
    @Nullable
    T getElementById(long id);

    /**
     * Retrieves the entity represented by the provided ID.
     *
     * @param  id
     *         The ID of the entity
     *
     * @throws java.lang.NumberFormatException
     *         If the provided String is {@code null} or
     *         cannot be resolved to an unsigned long id
     *
     * @return Possibly-null entity for the specified ID
     */
    @Nullable
    default T getElementById(@Nonnull String id)
    {
        return getElementById(MiscUtil.parseSnowflake(id));
    }
}
