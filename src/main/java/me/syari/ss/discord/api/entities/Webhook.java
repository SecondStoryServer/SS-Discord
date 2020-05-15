

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.managers.WebhookManager;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface Webhook extends ISnowflake, IFakeable
{

    @Nonnull
    JDA getJDA();


    @Nonnull
    WebhookType getType();


    @Nonnull
    Guild getGuild();


    @Nonnull
    TextChannel getChannel();


    @Nullable
    Member getOwner();


    @Nonnull
    User getDefaultUser();


    @Nonnull
    String getName();


    @Nullable
    String getToken();


    @Nonnull
    String getUrl();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete(@Nonnull String token);


    @Nonnull
    WebhookManager getManager();
}
