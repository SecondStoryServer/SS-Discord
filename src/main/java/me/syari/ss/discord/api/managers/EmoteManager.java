package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;


public interface EmoteManager extends Manager<EmoteManager> {

    long NAME = 0x1;

    long ROLES = 0x2;


    @Nonnull
    @Override
    EmoteManager reset(long fields);


    @Nonnull
    @Override
    EmoteManager reset(long... fields);


    @Nonnull
    default Guild getGuild() {
        return getEmote().getGuild();
    }


    @Nonnull
    Emote getEmote();


    @Nonnull
    @CheckReturnValue
    EmoteManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    EmoteManager setRoles(@Nullable Set<Role> roles);
}
