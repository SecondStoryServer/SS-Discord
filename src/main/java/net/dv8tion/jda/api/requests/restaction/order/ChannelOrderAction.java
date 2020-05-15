

package net.dv8tion.jda.api.requests.restaction.order;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Implementation of {@link OrderAction OrderAction}
 * to modify the order of {@link net.dv8tion.jda.api.entities.GuildChannel Channels} for a {@link net.dv8tion.jda.api.entities.Guild Guild}.
 * <br>To apply the changes you must finish the {@link net.dv8tion.jda.api.requests.RestAction RestAction}.
 *
 * <p>Before you can use any of the {@code move} methods
 * you must use either {@link #selectPosition(Object) selectPosition(GuildChannel)} or {@link #selectPosition(int)}!
 *
 * @since 3.0
 *
 * @see   net.dv8tion.jda.api.entities.Guild
 * @see   net.dv8tion.jda.api.entities.Guild#modifyTextChannelPositions()
 * @see   net.dv8tion.jda.api.entities.Guild#modifyVoiceChannelPositions()
 * @see   net.dv8tion.jda.api.entities.Guild#modifyCategoryPositions()
 * @see   CategoryOrderAction
 */
public interface ChannelOrderAction extends OrderAction<GuildChannel, ChannelOrderAction>
{
    /**
     * The {@link net.dv8tion.jda.api.entities.Guild Guild} which holds
     * the channels from {@link #getCurrentOrder()}
     *
     * @return The corresponding {@link net.dv8tion.jda.api.entities.Guild Guild}
     */
    @Nonnull
    Guild getGuild();

    /**
     * The sorting bucket for this order action.
     * <br>Multiple different {@link net.dv8tion.jda.api.entities.ChannelType ChannelTypes} can
     * share a common sorting bucket.
     *
     * @return The sorting bucket
     */
    int getSortBucket();

    /**
     * The {@link net.dv8tion.jda.api.entities.ChannelType ChannelTypes} for the {@link #getSortBucket() sorting bucket}.
     *
     * @return The channel types
     *
     * @see    net.dv8tion.jda.api.entities.ChannelType#fromSortBucket(int)
     */
    @Nonnull
    default EnumSet<ChannelType> getChannelTypes()
    {
        return ChannelType.fromSortBucket(getSortBucket());
    }
}
