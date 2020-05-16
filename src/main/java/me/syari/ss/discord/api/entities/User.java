package me.syari.ss.discord.api.entities;


import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public interface User extends IMentionable, IFakeable {
    @Nonnull
    String getName();


    @Nonnull
    String getDiscriminator();


    @Nonnull
    String getAsTag();


    boolean isBot();


    @Nonnull
    JDA getJDA();
}
