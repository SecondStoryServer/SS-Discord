package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;

import javax.annotation.Nonnull;


public interface Presence {


    @Nonnull
    OnlineStatus getStatus();


}
