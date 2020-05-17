package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.JDA;

import org.jetbrains.annotations.NotNull;


public interface SessionController {

    int IDENTIFY_DELAY = 5;


    void appendSession(@NotNull SessionConnectNode node);


    void removeSession(@NotNull SessionConnectNode node);


    long getGlobalRatelimit();


    void setGlobalRatelimit(long ratelimit);


    @NotNull
    String getGateway(@NotNull JDA api);


    interface SessionConnectNode {
        void run(boolean isLast) throws InterruptedException;
    }
}
