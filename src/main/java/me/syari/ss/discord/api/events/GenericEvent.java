

package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

public interface GenericEvent
{

    @Nonnull
    JDA getJDA();


    long getResponseNumber();
}
