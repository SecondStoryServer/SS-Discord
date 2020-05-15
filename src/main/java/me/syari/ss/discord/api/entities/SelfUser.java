
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.managers.AccountManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface SelfUser extends User
{


    boolean isVerified();


    boolean isMfaEnabled();


    @Nonnull
    String getEmail();


    boolean isMobile();


    boolean isNitro();


    @Nullable
    String getPhoneNumber();


    long getAllowedFileSize();


    @Nonnull
    AccountManager getManager();
}
