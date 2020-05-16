package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.entities.SelfUser;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface AccountManager extends Manager<AccountManager> {

    long NAME = 0x1;


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
    AccountManager setName(@Nonnull String name, @Nullable String currentPassword);


}
