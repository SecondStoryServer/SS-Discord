package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public interface SessionController {

    int IDENTIFY_DELAY = 5;


    void appendSession(@Nonnull SessionConnectNode node);


    void removeSession(@Nonnull SessionConnectNode node);


    long getGlobalRatelimit();


    void setGlobalRatelimit(long ratelimit);


    @Nonnull
    String getGateway(@Nonnull JDA api);


    interface SessionConnectNode {
        void run(boolean isLast) throws InterruptedException;
    }
}
