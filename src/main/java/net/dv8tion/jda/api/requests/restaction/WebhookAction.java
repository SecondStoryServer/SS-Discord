

package net.dv8tion.jda.api.requests.restaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

/**
 * {@link net.dv8tion.jda.api.entities.Webhook Webhook} Builder system created as an extension of {@link net.dv8tion.jda.api.requests.RestAction}
 * <br>Provides an easy way to gather and deliver information to Discord to create {@link net.dv8tion.jda.api.entities.Webhook Webhooks}.
 *
 * @see net.dv8tion.jda.api.entities.TextChannel#createWebhook(String)
 */
public interface WebhookAction extends AuditableRestAction<Webhook>
{
    @Nonnull
    @Override
    WebhookAction setCheck(@Nullable BooleanSupplier checks);

    /**
     * The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to create this webhook in
     *
     * @return The channel
     */
    @Nonnull
    TextChannel getChannel();

    /**
     * The {@link net.dv8tion.jda.api.entities.Guild Guild} to create this webhook in
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
     *         An {@link net.dv8tion.jda.api.entities.Icon Icon} for the new avatar.
     *         Or null to use default avatar.
     *
     * @return The current WebhookAction for chaining convenience.
     */
    @Nonnull
    @CheckReturnValue
    WebhookAction setAvatar(@Nullable Icon icon);
}
