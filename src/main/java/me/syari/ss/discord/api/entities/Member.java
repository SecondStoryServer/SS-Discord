package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Member extends IMentionable {

    @Nonnull
    User getUser();


    @Nullable
    String getNickname();


    @Nonnull
    String getDisplayName();


}
