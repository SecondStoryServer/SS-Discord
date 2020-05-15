

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BooleanSupplier;


public interface MemberAction extends RestAction<Void>
{
    @Nonnull
    @Override
    MemberAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    String getAccessToken();


    @Nonnull
    String getUserId();


    @Nullable
    User getUser();


    @Nonnull
    Guild getGuild();


    @Nonnull
    @CheckReturnValue
    MemberAction setNickname(@Nullable String nick);


    @Nonnull
    @CheckReturnValue
    MemberAction setRoles(@Nullable Collection<Role> roles);


    @Nonnull
    @CheckReturnValue
    MemberAction setRoles(@Nullable Role... roles);


    @Nonnull
    @CheckReturnValue
    MemberAction setMute(boolean mute);


    @Nonnull
    @CheckReturnValue
    MemberAction setDeafen(boolean deaf);
}
