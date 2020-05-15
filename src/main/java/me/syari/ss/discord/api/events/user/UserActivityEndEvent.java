

package me.syari.ss.discord.api.events.user;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.user.update.GenericUserPresenceEvent;

import javax.annotation.Nonnull;


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
