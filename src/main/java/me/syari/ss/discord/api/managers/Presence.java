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


    @Nullable
    Activity getActivity();


    void setStatus(@Nullable OnlineStatus status);


    void setActivity(@Nullable Activity activity);


    void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity, boolean idle);


    void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity);


}
