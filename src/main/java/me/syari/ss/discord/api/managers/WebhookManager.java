

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.Webhook;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface WebhookManager extends Manager<WebhookManager>
{

    long NAME    = 0x1;

    long CHANNEL = 0x2;

    long AVATAR  = 0x4;


    @Nonnull
    @Override
    WebhookManager reset(long fields);


    @Nonnull
    @Override
    WebhookManager reset(long... fields);


    @Nonnull
    Webhook getWebhook();


    @Nonnull
    default TextChannel getChannel()
    {
        return getWebhook().getChannel();
    }


    @Nonnull
    default Guild getGuild()
    {
        return getWebhook().getGuild();
    }


    @Nonnull
    @CheckReturnValue
    WebhookManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    WebhookManager setAvatar(@Nullable Icon icon);


    @Nonnull
    @CheckReturnValue
    WebhookManager setChannel(@Nonnull TextChannel channel);
}
