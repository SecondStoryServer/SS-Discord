

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.managers.EmoteManager;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.internal.utils.PermissionUtil;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface Emote extends IMentionable, IFakeable
{

    String ICON_URL = "https://cdn.discordapp.com/emojis/%s.%s";


    @Nullable
    Guild getGuild();


    @Nonnull
    List<Role> getRoles();


    @Deprecated
    @DeprecatedSince("3.8.0")
    @ReplaceWith("canProvideRoles()")
    default boolean hasRoles()
    {
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
    default String getImageUrl()
    {
        return String.format(ICON_URL, getId(), isAnimated() ? "gif" : "png");
    }


    @Nonnull
    @Override
    default String getAsMention()
    {
        return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
    }


    default boolean canInteract(Member issuer)
    {
        return PermissionUtil.canInteract(issuer, this);
    }


    default boolean canInteract(User issuer, MessageChannel channel)
    {
        return PermissionUtil.canInteract(issuer, this, channel);
    }


    default boolean canInteract(User issuer, MessageChannel channel, boolean botOverride)
    {
        return PermissionUtil.canInteract(issuer, this, channel, botOverride);
    }
}
