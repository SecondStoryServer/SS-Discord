package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.managers.AccountManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface SelfUser extends User {

    boolean isVerified();

    @Nonnull
    AccountManager getManager();
}
