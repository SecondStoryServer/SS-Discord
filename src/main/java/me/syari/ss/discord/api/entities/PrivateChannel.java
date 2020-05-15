
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;


public interface PrivateChannel extends MessageChannel, IFakeable
{

    @Nonnull
    User getUser();


    @Nonnull
    JDA getJDA();


    @Nonnull
    @CheckReturnValue
    RestAction<Void> close();
}
