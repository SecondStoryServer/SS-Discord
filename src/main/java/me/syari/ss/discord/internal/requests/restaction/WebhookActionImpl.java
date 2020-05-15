

package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.WebhookAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.Webhook;
import me.syari.ss.discord.api.utils.data.DataObject;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;


public class WebhookActionImpl extends AuditableRestActionImpl<Webhook> implements WebhookAction
{
    protected final TextChannel channel;
    protected String name;
    protected Icon avatar = null;

    public WebhookActionImpl(JDA api, TextChannel channel, String name)
    {
        super(api, Route.Channels.CREATE_WEBHOOK.compile(channel.getId()));
        this.channel = channel;
        this.name = name;
    }

    @Nonnull
    @Override
    public WebhookActionImpl setCheck(BooleanSupplier checks)
    {
        return (WebhookActionImpl) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public TextChannel getChannel()
    {
        return channel;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookActionImpl setName(@Nonnull String name)
    {
        Checks.notNull(name, "Webhook name");
        Checks.check(name.length() >= 2 && name.length() <= 100, "The webhook name must be in the range of 2-100!");

        this.name = name;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookActionImpl setAvatar(Icon icon)
    {
        this.avatar = icon;
        return this;
    }

    @Override
    public RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();
        object.put("name",   name);
        object.put("avatar", avatar != null ? avatar.getEncoding() : null);

        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(Response response, Request<Webhook> request)
    {
        DataObject json = response.getObject();
        Webhook webhook = api.getEntityBuilder().createWebhook(json);

        request.onSuccess(webhook);
    }
}
