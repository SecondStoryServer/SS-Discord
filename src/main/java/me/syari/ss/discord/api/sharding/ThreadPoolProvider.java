

package me.syari.ss.discord.api.sharding;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;

/**
 * Called by {@link DefaultShardManager} when building a JDA instance.
 * <br>Every time a JDA instance is built, the manager will first call {@link #provide(int)} followed by
 * a call to {@link #shouldShutdownAutomatically(int)}.
 *
 * @param <T>
 *        The type of executor
 */
public interface ThreadPoolProvider<T extends ExecutorService>
{
    /**
     * Provides an instance of the specified executor, or null
     *
     * @param  shardId
     *         The current shard id
     *
     * @return The Executor Service
     */
    @Nullable
    T provide(int shardId);

    /**
     * Whether the previously provided executor should be shutdown by {@link JDA#shutdown()}.
     *
     * @param  shardId
     *         The current shard id
     *
     * @return True, if the executor should be shutdown by JDA
     */
    default boolean shouldShutdownAutomatically(int shardId)
    {
        return false;
    }
}
