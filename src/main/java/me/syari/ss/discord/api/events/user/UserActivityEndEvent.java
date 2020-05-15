

package me.syari.ss.discord.api.events.user;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.user.update.GenericUserPresenceEvent;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link User User} has stopped an {@link Activity}
 * in a {@link Guild}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>This is fired for every {@link Guild} the user is part of. If the title of a stream
 * changes a start event is fired before an end event which will replace the activity.
 *
 * <p>The activities of the {@link Member} are updated before all start/end events are fired.
 * This means you can check {@link Member#getActivities()} when handling this event and it
 * will already contain all new activities, even ones that have not yet fired the start event.
 *
 * <p>To check whether the activity has concluded rather than was replaced due to an update
 * of one of its properties such as name you can check {@link Member#getActivities()}.
 * Iterate the list of activities and check if an activity of the same {@link Activity#getType() type}
 * exists, if that is the case it was replaced and not finished.
 */
public class UserActivityEndEvent extends GenericUserEvent implements GenericUserPresenceEvent
{
    private final Activity oldActivity;
    private final Member member;

    public UserActivityEndEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull Activity oldActivity)
    {
        super(api, responseNumber, member.getUser());
        this.oldActivity = oldActivity;
        this.member = member;
    }

    /**
     * The old activity
     *
     * @return The old activity
     */
    @Nonnull
    public Activity getOldActivity()
    {
        return oldActivity;
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return member.getGuild();
    }

    @Nonnull
    @Override
    public Member getMember()
    {
        return member;
    }
}
