

package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.internal.JDAImpl;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Indicates that the {@link Activity Activity} order of a {@link User User} changes.
 * <br>As with any presence updates this happened for a {@link Member Member} in a Guild!
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions} to be enabled.
 *
 * <p>Can be used to retrieve the User who changed their Activities and their previous Activities.
 *
 * <p>Identifier: {@code activity_order}
 */
public class UserUpdateActivityOrderEvent extends GenericUserUpdateEvent<List<Activity>> implements GenericUserPresenceEvent
{
    public static final String IDENTIFIER = "activity_order";

    private final Member member;

    public UserUpdateActivityOrderEvent(@Nonnull JDAImpl api, long responseNumber, @Nonnull List<Activity> previous, @Nonnull Member member)
    {
        super(api, responseNumber, member.getUser(), previous, member.getActivities(), IDENTIFIER);
        this.member = member;
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

    @Nonnull
    @Override
    public List<Activity> getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public List<Activity> getNewValue()
    {
        return super.getNewValue();
    }
}
