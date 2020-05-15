

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.Permission;

import javax.annotation.Nonnull;


public interface ListedEmote extends Emote
{

    @Nonnull
    User getUser();


    boolean hasUser();
}
