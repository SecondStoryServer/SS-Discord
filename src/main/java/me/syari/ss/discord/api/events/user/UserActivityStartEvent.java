

package me.syari.ss.discord.api.events.user;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.user.update.GenericUserPresenceEvent;

import javax.annotation.Nonnull;


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
