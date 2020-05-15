

package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.sharding.DefaultShardManagerBuilder;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;


@FunctionalInterface
public interface ChunkingFilter
{

    ChunkingFilter ALL = (x) -> true;

    ChunkingFilter NONE = (x) -> false;


    boolean filter(long guildId);


    @Nonnull
    static ChunkingFilter include(@Nonnull long... ids)
    {
        Checks.notNull(ids, "ID array");
        return (guild) -> {
            for (long id : ids)
            {
                if (id == guild)
                    return true;
            }
            return false;
        };
    }


    @Nonnull
    static ChunkingFilter exclude(@Nonnull long... ids)
    {
        Checks.notNull(ids, "ID array");
        return (guild) -> {
            for (long id : ids)
            {
                if (id == guild)
                    return false;
            }
            return true;
        };
    }
}
