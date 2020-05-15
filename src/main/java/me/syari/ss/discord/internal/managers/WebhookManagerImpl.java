

package me.syari.ss.discord.internal.managers;

import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.Webhook;
import me.syari.ss.discord.api.managers.WebhookManager;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class WebhookManagerImpl extends ManagerBase<WebhookManager> implements WebhookManager
{
    protected final Webhook webhook;
    protected String name;
    protected String channel;
    protected Icon avatar;

    /**
     * Creates a new WebhookManager instance
     *
     * @param webhook
     *        The target {@link Webhook Webhook} to modify
     */
    public WebhookManagerImpl(Webhook webhook)
    {
        super(webhook.getJDA(), Route.Webhooks.MODIFY_WEBHOOK.compile(webhook.getId()));
        this.webhook = webhook;
        if (isPermissionChecksEnabled())
            checkPermissions();
    }

    @Nonnull
    @Override
    public Webhook getWebhook()
    {
        return webhook;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookManagerImpl reset(long fields)
    {
        super.reset(fields);
        if ((fields & NAME) == NAME)
            this.name = null;
        if ((fields & CHANNEL) == CHANNEL)
            this.channel = null;
        if ((fields & AVATAR) == AVATAR)
            this.avatar = null;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookManagerImpl reset(long... fields)
    {
        super.reset(fields);
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookManagerImpl reset()
    {
        super.reset();
        this.name = null;
        this.channel = null;
        this.avatar = null;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookManagerImpl setName(@Nonnull String name)
    {
        Checks.notBlank(name, "Name");
        this.name = name;
        set |= NAME;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookManagerImpl setAvatar(Icon icon)
    {
        this.avatar = icon;
        set |= AVATAR;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public WebhookManagerImpl setChannel(@Nonnull TextChannel channel)
    {
        Checks.notNull(channel, "Channel");
        Checks.check(channel.getGuild().equals(getGuild()), "Channel is not from the same guild");
        this.channel = channel.getId();
        set |= CHANNEL;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject data = DataObject.empty();
        if (shouldUpdate(NAME))
            data.put("name", name);
        if (shouldUpdate(CHANNEL))
            data.put("channel_id", channel);
        if (shouldUpdate(AVATAR))
            data.put("avatar", avatar == null ? null : avatar.getEncoding());

        return getRequestBody(data);
    }

    @Override
    protected boolean checkPermissions()
    {
        if (!getGuild().getSelfMember().hasPermission(getChannel(), Permission.MANAGE_WEBHOOKS))
            throw new InsufficientPermissionException(getChannel(), Permission.MANAGE_WEBHOOKS);
        return super.checkPermissions();
    }
}
