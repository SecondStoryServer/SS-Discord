package me.syari.ss.discord.api.entities;


import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface User extends IMentionable, IFakeable {


    @Nonnull
    String getName();


    @Nonnull
    String getDiscriminator();


    @Nullable
    String getAvatarId();


    @Nonnull
    String getAsTag();


    boolean hasPrivateChannel();


    boolean isBot();


    @Nonnull
    JDA getJDA();
}
