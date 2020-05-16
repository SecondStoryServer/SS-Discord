package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface Presence {

    @Nonnull
    JDA getJDA();


    @Nonnull
    OnlineStatus getStatus();


    void setStatus(@Nullable OnlineStatus status);


    void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity, boolean idle);


}
