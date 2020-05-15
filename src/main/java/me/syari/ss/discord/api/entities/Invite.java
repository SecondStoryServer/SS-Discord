

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild.VerificationLevel;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.internal.entities.InviteImpl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;


public interface Invite
{

    @Nonnull
    static RestAction<Invite> resolve(@Nonnull final JDA api, @Nonnull final String code)
    {
        return resolve(api, code, false);
    }
    

    @Nonnull
    static RestAction<Invite> resolve(@Nonnull final JDA api, @Nonnull final String code, final boolean withCounts)
    {
        return InviteImpl.resolve(api, code, withCounts);
    }


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    @CheckReturnValue
    RestAction<Invite> expand();


    @Nonnull
    Invite.InviteType getType();


    @Nullable
    Channel getChannel();


    @Nonnull
    String getCode();


    @Nullable
    Group getGroup();


    @Nonnull
    default String getUrl()
    {
        return "https://discord.gg/" + getCode();
    }


    @Nonnull
    @Deprecated
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getTimeCreated()")
    OffsetDateTime getCreationTime();


    @Nullable
    Guild getGuild();


    @Nullable
    User getInviter();


    @Nonnull
    JDA getJDA();


    int getMaxAge();


    int getMaxUses();


    @Nonnull
    OffsetDateTime getTimeCreated();


    int getUses();


    boolean isExpanded();


    boolean isTemporary();


    interface Channel extends ISnowflake
    {

        @Nonnull
        String getName();


        @Nonnull
        ChannelType getType();
    }


    interface Guild extends ISnowflake
    {

        @Nullable
        String getIconId();


        @Nullable
        String getIconUrl();


        @Nonnull
        String getName();


        @Nullable
        String getSplashId();


        @Nullable
        String getSplashUrl();
        

        @Nonnull
        VerificationLevel getVerificationLevel();
        

        int getOnlineCount();
        

        int getMemberCount();


        @Nonnull
        Set<String> getFeatures();
    }


    interface Group extends ISnowflake
    {

        @Nullable
        String getIconId();


        @Nullable
        String getIconUrl();


        @Nullable
        String getName();


        @Nullable
        List<String> getUsers();
    }


    enum InviteType
    {
        GUILD,
        GROUP,
        UNKNOWN
    }
}
