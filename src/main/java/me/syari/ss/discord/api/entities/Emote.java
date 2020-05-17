package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public interface Emote extends IMentionable {

    @Nonnull
    String getName();


    boolean isAnimated();


    @Nonnull
    @Override
    default String getAsMention() {
        return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
    }


}
