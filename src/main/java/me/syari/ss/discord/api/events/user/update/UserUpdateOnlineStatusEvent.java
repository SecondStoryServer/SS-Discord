

package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


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

    
    @Nonnull
    public OnlineStatus getOldOnlineStatus()
    {
        return getOldValue();
    }

    
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
