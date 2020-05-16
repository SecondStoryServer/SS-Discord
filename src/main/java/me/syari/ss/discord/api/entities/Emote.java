package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.managers.EmoteManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface Emote extends IMentionable, IFakeable {

    @Nonnull
    String getName();


    boolean isAnimated();


    @Nonnull
    @Override
    default String getAsMention() {
        return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
    }


}
