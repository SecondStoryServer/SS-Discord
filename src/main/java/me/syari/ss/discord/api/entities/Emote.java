package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.managers.EmoteManager;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface Emote extends IMentionable, IFakeable {


    @Nullable
    Guild getGuild();


    @Nonnull
    List<Role> getRoles();


    @Deprecated
    @DeprecatedSince("3.8.0")
    @ReplaceWith("canProvideRoles()")
    default boolean hasRoles() {
        return canProvideRoles();
    }


    boolean canProvideRoles();


    @Nonnull
    String getName();


    boolean isManaged();


    @Nonnull
    JDA getJDA();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    EmoteManager getManager();


    boolean isAnimated();


    @Nonnull
    @Override
    default String getAsMention() {
        return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
    }


}
