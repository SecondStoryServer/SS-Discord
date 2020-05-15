

package net.dv8tion.jda.api.events.user.update;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;

import javax.annotation.Nonnull;

/**
 * Indicates that the presence of a {@link net.dv8tion.jda.api.entities.User User} has changed.
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 * <br>Users don't have presences directly, this is fired when a {@link net.dv8tion.jda.api.entities.Member Member} from a {@link net.dv8tion.jda.api.entities.Guild Guild}
 * changes their presence.
 *
 * <p>Can be used to track the presence updates of members.
 */
public interface GenericUserPresenceEvent extends GenericEvent
{
    /**
     * Guild in which the presence has changed.
     *
     * @return The guild
     */
    @Nonnull
    Guild getGuild();

    /**
     * Member who changed their presence.
     *
     * @return The member
     */
    @Nonnull
    Member getMember();
}
