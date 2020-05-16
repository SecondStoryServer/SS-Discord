package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;


public interface EmoteManager extends Manager<EmoteManager> {


    @Nonnull
    @Override
    EmoteManager reset(long fields);


    @Nonnull
    @Override
    EmoteManager reset(long... fields);


    @Nonnull
    Emote getEmote();


    @Nonnull
    @CheckReturnValue
    EmoteManager setName(@Nonnull String name);


}
