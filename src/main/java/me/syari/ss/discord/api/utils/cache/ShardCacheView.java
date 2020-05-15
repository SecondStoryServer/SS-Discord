
package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface ShardCacheView extends CacheView<JDA>
{
    
    @Nullable
    JDA getElementById(int id);

    
    @Nullable
    default JDA getElementById(@Nonnull String id)
    {
        return getElementById(Integer.parseUnsignedInt(id));
    }
}
