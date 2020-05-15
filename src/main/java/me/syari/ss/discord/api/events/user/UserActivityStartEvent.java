

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
 * Indicates that a {@link User User} has started an {@link Activity}
 * in a {@link Guild}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>This is fired for every {@link Guild} the user is part of. If the title of a stream
 * changes a start event is fired before an end event which will replace the activity.
 *
 * <p>The activities of the {@link Member} are updated before all the start/end events are fired.
 * This means you can check {@link Member#getActivities()} when handling this event and it will already
 * contain all new activities, even ones that have not yet fired the start event.
 */
public class UserActivityStartEvent extends GenericUserEvent implements GenericUserPresenceEvent
{
    private final Activity newActivity;
    private final Member member;

    public UserActivityStartEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull Activity newActivity)
    {
        super(api, responseNumber, member.getUser());
        this.newActivity = newActivity;
        this.member = member;
    }

    /**
     * The new activity
     *
     * @return The activity
     */
    public Activity getNewActivity()
    {
        return newActivity;
    }

    @Override
    public Guild getGuild()
    {
        return member.getGuild();
    }

    @Override
    public Member getMember()
    {
        return member;
    }
}
