

package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.GenericEvent;

import javax.annotation.Nonnull;

/**
 * Indicates that the presence of a {@link User User} has changed.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 * <br>Users don't have presences directly, this is fired when a {@link Member Member} from a {@link Guild Guild}
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
