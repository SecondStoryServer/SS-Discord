

package me.syari.ss.discord.api.sharding;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;


public interface ThreadPoolProvider<T extends ExecutorService>
{

    @Nullable
    T provide(int shardId);


    default boolean shouldShutdownAutomatically(int shardId)
    {
        return false;
    }
}
