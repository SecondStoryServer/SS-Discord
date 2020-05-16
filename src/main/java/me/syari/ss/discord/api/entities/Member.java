package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Member extends IMentionable, IFakeable {

    @Nonnull
    User getUser();


    @Nullable
    String getNickname();


    @Nonnull
    String getDisplayName();


}
