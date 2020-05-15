

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.Webhook;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

/**
 * {@link Webhook Webhook} Builder system created as an extension of {@link RestAction}
 * <br>Provides an easy way to gather and deliver information to Discord to create {@link Webhook Webhooks}.
 *
 * @see TextChannel#createWebhook(String)
 */
public interface WebhookAction extends AuditableRestAction<Webhook>
{
    @Nonnull
    @Override
    WebhookAction setCheck(@Nullable BooleanSupplier checks);

    /**
     * The {@link TextChannel TextChannel} to create this webhook in
     *
     * @return The channel
     */
    @Nonnull
    TextChannel getChannel();

    /**
     * The {@link Guild Guild} to create this webhook in
     *
     * @return The guild
     */
    @Nonnull
    default Guild getGuild()
    {
        return getChannel().getGuild();
    }

    /**
     * Sets the <b>Name</b> for the custom Webhook User
     *
     * @param  name
     *         A not-null String name for the new Webhook user.
     *
     * @throws IllegalArgumentException
     *         If the specified name is not in the range of 2-100.
     *
     * @return The current WebhookAction for chaining convenience.
     */
    @Nonnull
    @CheckReturnValue
    WebhookAction setName(@Nonnull String name);

    /**
     * Sets the <b>Avatar</b> for the custom Webhook User
     *
     * @param  icon
     *         An {@link Icon Icon} for the new avatar.
     *         Or null to use default avatar.
     *
     * @return The current WebhookAction for chaining convenience.
     */
    @Nonnull
    @CheckReturnValue
    WebhookAction setAvatar(@Nullable Icon icon);
}
