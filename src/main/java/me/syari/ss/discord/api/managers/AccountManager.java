

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.entities.SelfUser;
import me.syari.ss.discord.api.exceptions.AccountTypeException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface AccountManager extends Manager<AccountManager>
{

    long NAME = 0x1;

    long AVATAR = 0x2;

    long EMAIL = 0x4;

    long PASSWORD = 0x8;


    @Nonnull
    SelfUser getSelfUser();


    @Nonnull
    @Override
    @CheckReturnValue
    AccountManager reset(long fields);


    @Nonnull
    @Override
    @CheckReturnValue
    AccountManager reset(long... fields);


    @Nonnull
    @CheckReturnValue
    default AccountManager setName(@Nonnull String name)
    {
        return setName(name, null);
    }


    @Nonnull
    @CheckReturnValue
    AccountManager setName(@Nonnull String name, @Nullable String currentPassword);


    @Nonnull
    @CheckReturnValue
    default AccountManager setAvatar(@Nullable Icon avatar)
    {
        return setAvatar(avatar, null);
    }


    @Nonnull
    @CheckReturnValue
    AccountManager setAvatar(@Nullable Icon avatar, @Nullable String currentPassword);


    @Nonnull
    @CheckReturnValue
    AccountManager setEmail(@Nonnull String email, @Nonnull String currentPassword);


    @Nonnull
    @CheckReturnValue
    AccountManager setPassword(@Nonnull String newPassword, @Nonnull String currentPassword);
}
