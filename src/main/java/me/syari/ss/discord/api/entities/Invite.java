

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.internal.entities.InviteImpl;
import javax.annotation.Nonnull;
import java.time.OffsetDateTime;


public interface Invite
{


    @Nonnull
    static RestAction<Invite> resolve(@Nonnull final JDA api, @Nonnull final String code, final boolean withCounts)
    {
        return InviteImpl.resolve(api, code, withCounts);
    }


    @Nonnull
    String getCode();


    @Nonnull
    @Deprecated
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getTimeCreated()")
    OffsetDateTime getCreationTime();


    @Nonnull
    JDA getJDA();


    @Nonnull
    OffsetDateTime getTimeCreated();


    interface Channel extends ISnowflake
    {


        @Nonnull
        ChannelType getType();
    }


    interface Guild extends ISnowflake
    {


    }


    interface Group extends ISnowflake
    {


    }


    enum InviteType
    {
        GUILD,
        GROUP,
        UNKNOWN
    }
}
