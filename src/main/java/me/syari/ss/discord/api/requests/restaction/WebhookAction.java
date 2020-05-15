

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


public interface WebhookAction extends AuditableRestAction<Webhook>
{
    @Nonnull
    @Override
    WebhookAction setCheck(@Nullable BooleanSupplier checks);

    
    @Nonnull
    TextChannel getChannel();

    
    @Nonnull
    default Guild getGuild()
    {
        return getChannel().getGuild();
    }

    
    @Nonnull
    @CheckReturnValue
    WebhookAction setName(@Nonnull String name);

    
    @Nonnull
    @CheckReturnValue
    WebhookAction setAvatar(@Nullable Icon icon);
}
