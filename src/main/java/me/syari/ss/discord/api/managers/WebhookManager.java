

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

/**
 * Manager providing functionality to update one or more fields for a {@link Webhook Webhook}.
 *
 * <p><b>Example</b>
 * <pre>{@code
 * manager.setName("GitHub Webhook")
 *        .setChannel(channel)
 *        .queue();
 * manager.reset(WebhookManager.NAME | WebhookManager.AVATAR)
 *        .setName("Meme Feed")
 *        .setAvatar(null)
 *        .queue();
 * }</pre>
 *
 * @see Webhook#getManager()
 */
public interface WebhookManager extends Manager<WebhookManager>
{
    /** Used to reset the name field */
    long NAME    = 0x1;
    /** Used to reset the channel field */
    long CHANNEL = 0x2;
    /** Used to reset the avatar field */
    long AVATAR  = 0x4;

    /**
     * Resets the fields specified by the provided bit-flag pattern.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(WebhookManager.CHANNEL | WebhookManager.NAME);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #AVATAR}</li>
     *     <li>{@link #CHANNEL}</li>
     * </ul>
     *
     * @param  fields
     *         Integer value containing the flags to reset.
     *
     * @return WebhookManager for chaining convenience
     */
    @Nonnull
    @Override
    WebhookManager reset(long fields);

    /**
     * Resets the fields specified by the provided bit-flag patterns.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(WebhookManager.CHANNEL, WebhookManager.NAME);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #AVATAR}</li>
     *     <li>{@link #CHANNEL}</li>
     * </ul>
     *
     * @param  fields
     *         Integer values containing the flags to reset.
     *
     * @return WebhookManager for chaining convenience
     */
    @Nonnull
    @Override
    WebhookManager reset(long... fields);

    /**
     * The target {@link Webhook Webhook}
     * that will be modified by this manager
     *
     * @return The target {@link Webhook Webhook}
     */
    @Nonnull
    Webhook getWebhook();

    /**
     * The {@link TextChannel TextChannel} this Manager's
     * {@link Webhook Webhook} is in.
     * <br>This is logically the same as calling {@code getWebhook().getChannel()}
     *
     * @return The parent {@link TextChannel TextChannel}
     */
    @Nonnull
    default TextChannel getChannel()
    {
        return getWebhook().getChannel();
    }

    /**
     * The {@link Guild Guild} this Manager's
     * {@link Webhook Webhook} is in.
     * <br>This is logically the same as calling {@code getWebhook().getGuild()}
     *
     * @return The parent {@link Guild Guild}
     */
    @Nonnull
    default Guild getGuild()
    {
        return getWebhook().getGuild();
    }

    /**
     * Sets the <b><u>default name</u></b> of the selected {@link Webhook Webhook}.
     *
     * <p>A webhook name <b>must not</b> be {@code null} or blank!
     *
     * @param  name
     *         The new default name for the selected {@link Webhook Webhook}
     *
     * @throws IllegalArgumentException
     *         If the provided name is {@code null} or blank
     *
     * @return WebhookManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    WebhookManager setName(@Nonnull String name);

    /**
     * Sets the <b><u>default avatar</u></b> of the selected {@link Webhook Webhook}.
     *
     * @param  icon
     *         The new default avatar {@link Icon Icon}
     *         for the selected {@link Webhook Webhook}
     *         or {@code null} to reset
     *
     * @return WebhookManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    WebhookManager setAvatar(@Nullable Icon icon);

    /**
     * Sets the {@link TextChannel TextChannel} of the selected {@link Webhook Webhook}.
     *
     * <p>A webhook channel <b>must not</b> be {@code null} and <b>must</b> be in the same {@link Guild Guild}!
     *
     * @param  channel
     *         The new {@link TextChannel TextChannel}
     *         for the selected {@link Webhook Webhook}
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have the Permission {@link Permission#MANAGE_WEBHOOKS MANAGE_WEBHOOKS}
     *         in the specified TextChannel
     * @throws IllegalArgumentException
     *         If the provided channel is {@code null} or from a different Guild
     *
     * @return WebhookManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    WebhookManager setChannel(@Nonnull TextChannel channel);
}
