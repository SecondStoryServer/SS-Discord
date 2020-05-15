

package net.dv8tion.jda.api.events.user.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link OnlineStatus OnlineStatus} of a {@link net.dv8tion.jda.api.entities.User User} changed.
 * <br>As with any presence updates this happened for a {@link net.dv8tion.jda.api.entities.Member Member} in a Guild!
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions} to be enabled.
 *
 * <p>Can be used to retrieve the User who changed their status and their previous status.
 *
 * <p>Identifier: {@code status}
 */
public class UserUpdateOnlineStatusEvent extends GenericUserUpdateEvent<OnlineStatus> implements GenericUserPresenceEvent
{
    public static final String IDENTIFIER = "status";

    private final Guild guild;
    private final Member member;

    public UserUpdateOnlineStatusEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull OnlineStatus oldOnlineStatus)
    {
        super(api, responseNumber, member.getUser(), oldOnlineStatus, member.getOnlineStatus(), IDENTIFIER);
        this.guild = member.getGuild();
        this.member = member;
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Nonnull
    @Override
    public Member getMember()
    {
        return member;
    }

    /**
     * The old status
     *
     * @return The old status
     */
    @Nonnull
    public OnlineStatus getOldOnlineStatus()
    {
        return getOldValue();
    }

    /**
     * The new status
     *
     * @return The new status
     */
    @Nonnull
    public OnlineStatus getNewOnlineStatus()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public OnlineStatus getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public OnlineStatus getNewValue() {
        return super.getNewValue();
    }
}
