package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.Webhook;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;


public interface WebhookManager extends Manager<WebhookManager> {


    @Nonnull
    @Override
    WebhookManager reset(long fields);


    @Nonnull
    @Override
    WebhookManager reset(long... fields);


    @Nonnull
    Webhook getWebhook();


    @Nonnull
    default TextChannel getChannel() {
        return getWebhook().getChannel();
    }


    @Nonnull
    default Guild getGuild() {
        return getWebhook().getGuild();
    }


    @Nonnull
    @CheckReturnValue
    WebhookManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    WebhookManager setChannel(@Nonnull TextChannel channel);
}
