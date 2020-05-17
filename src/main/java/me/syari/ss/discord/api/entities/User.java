package me.syari.ss.discord.api.entities;


import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public interface User extends Mentionable {
    @Nonnull
    String getName();


    boolean isBot();


    @Nonnull
    JDA getJDA();
}
