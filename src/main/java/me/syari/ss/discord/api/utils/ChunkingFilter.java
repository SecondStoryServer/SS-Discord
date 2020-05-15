

package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.sharding.DefaultShardManagerBuilder;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;

/**
 * Filter function for member chunking of guilds.
 * <br>The filter decides based on the provided guild id whether chunking should be done
 * on guild initialization.
 *
 * @since 4.1.0
 *
 * @see   #ALL
 * @see   #NONE
 *
 * @see   JDABuilder#setChunkingFilter(ChunkingFilter) JDABuilder.setChunkingFilter(ChunkingFilter)
 * @see   DefaultShardManagerBuilder#setChunkingFilter(ChunkingFilter) DefaultShardManagerBuilder.setChunkingFilter(ChunkingFilter)
 */
@FunctionalInterface
public interface ChunkingFilter
{

    ChunkingFilter ALL = (x) -> true;

    ChunkingFilter NONE = (x) -> false;

    /**
     * Decide whether the specified guild should chunk members.
     *
     * @param  guildId
     *         The guild id
     *
     * @return True, if this guild should chunk
     */
    boolean filter(long guildId);

    /**
     * Factory method to chunk a whitelist of guild ids.
     * <br>All guilds that are not mentioned will use lazy loading.
     *
     * <p>This is useful to only chunk specific guilds like the hub server of a bot.
     *
     * @param  ids
     *         The ids that should be chunked
     *
     * @return The resulting filter
     */
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

    /**
     * Factory method to disable chunking for a blacklist of guild ids.
     * <br>All guilds that are not mentioned will use chunking.
     *
     * <p>This is useful when the bot is only in one very large server that
     * takes most of its memory and should be ignored instead.
     *
     * @param  ids
     *         The ids that should not be chunked
     *
     * @return The resulting filter
     */
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
