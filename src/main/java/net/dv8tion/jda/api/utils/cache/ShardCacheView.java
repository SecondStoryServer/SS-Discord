
package net.dv8tion.jda.api.utils.cache;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Read-only view on internal ShardManager cache of JDA instances.
 * <br>This can be useful to check information such as size without creating
 * an immutable snapshot first.
 *
 * @see CacheView CacheView for details on Efficient Memory Usage
 */
public interface ShardCacheView extends CacheView<JDA>
{
    /**
     * Retrieves the JDA instance represented by the provided shard ID.
     *
     * @param  id
     *         The ID of the entity
     *
     * @return Possibly-null entity for the specified shard ID
     */
    @Nullable
    JDA getElementById(int id);

    /**
     * Retrieves the JDA instance represented by the provided shard ID
     * or {@code null} if none of the connected shards match the provided id.
     *
     * @param  id
     *         The ID of the shard
     *
     * @throws java.lang.NumberFormatException
     *         If the provided String is {@code null} or
     *         cannot be resolved to an unsigned int id
     *
     * @return Possibly-null entity for the specified shard ID
     */
    @Nullable
    default JDA getElementById(@Nonnull String id)
    {
        return getElementById(Integer.parseUnsignedInt(id));
    }
}
