

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.Invite;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;


public interface InviteAction extends AuditableRestAction<Invite>
{
    @Nonnull
    @Override
    InviteAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    @CheckReturnValue
    InviteAction setMaxAge(@Nullable final Integer maxAge);


    @Nonnull
    @CheckReturnValue
    InviteAction setMaxAge(@Nullable final Long maxAge, @Nonnull final TimeUnit timeUnit);


    @Nonnull
    @CheckReturnValue
    InviteAction setMaxUses(@Nullable final Integer maxUses);


    @Nonnull
    @CheckReturnValue
    InviteAction setTemporary(@Nullable final Boolean temporary);


    @Nonnull
    @CheckReturnValue
    InviteAction setUnique(@Nullable final Boolean unique);
}
