package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.managers.AccountManager;

import javax.annotation.Nonnull;


public interface SelfUser extends User {
    @Nonnull
    AccountManager getManager();
}
