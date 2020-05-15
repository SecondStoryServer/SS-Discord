

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.managers.WebhookManager;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.managers.WebhookManagerImpl;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.AuditableRestActionImpl;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.ReentrantLock;


public class WebhookImpl implements Webhook
{
    protected volatile WebhookManager manager = null;

    private final ReentrantLock mngLock = new ReentrantLock();
    private final TextChannel channel;
    private final long id;
    private final WebhookType type;

    private Member owner;
    private User user;
    private String token;

    public WebhookImpl(TextChannel channel, long id, WebhookType type)
    {
        this.channel = channel;
        this.id = id;
        this.type = type;
    }

    @Nonnull
    @Override
    public WebhookType getType()
    {
        return type;
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return channel.getJDA();
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return channel.getGuild();
    }

    @Nonnull
    @Override
    public TextChannel getChannel()
    {
        return channel;
    }

    @Override
    public Member getOwner()
    {
        return owner;
    }

    @Nonnull
    @Override
    public User getDefaultUser()
    {
        return user;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return user.getName();
    }

    @Override
    public String getToken()
    {
        return token;
    }

    @Nonnull
    @Override
    public String getUrl()
    {
        return Requester.DISCORD_API_PREFIX + "webhooks/" + getId() + (getToken() == null ? "" : "/" + getToken());
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> delete()
    {
        if (token != null)
            return delete(token);

        if (!getGuild().getSelfMember().hasPermission(getChannel(), Permission.MANAGE_WEBHOOKS))
            throw new InsufficientPermissionException(getChannel(), Permission.MANAGE_WEBHOOKS);

        Route.CompiledRoute route = Route.Webhooks.DELETE_WEBHOOK.compile(getId());
        return new AuditableRestActionImpl<>(getJDA(), route);
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> delete(@Nonnull String token)
    {
        Checks.notNull(token, "Token");
        Route.CompiledRoute route = Route.Webhooks.DELETE_TOKEN_WEBHOOK.compile(getId(), token);
        return new AuditableRestActionImpl<>(getJDA(), route);
    }

    @Nonnull
    @Override
    public WebhookManager getManager()
    {
        WebhookManager mng = manager;
        if (mng == null)
        {
            mng = MiscUtil.locked(mngLock, () ->
            {
                if (manager == null)
                    manager = new WebhookManagerImpl(this);
                return manager;
            });
        }
        return mng;
    }

    @Override
    public long getIdLong()
    {
        return id;
    }

    @Override
    public boolean isFake()
    {
        return token == null;
    }

    /* -- Impl Setters -- */

    public WebhookImpl setOwner(Member member)
    {
        this.owner = member;
        return this;
    }

    public WebhookImpl setToken(String token)
    {
        this.token = token;
        return this;
    }

    public WebhookImpl setUser(User user)
    {
        this.user = user;
        return this;
    }

    /* -- Object Overrides -- */

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof WebhookImpl))
            return false;
        WebhookImpl impl = (WebhookImpl) obj;
        return impl.id == id;
    }

    @Override
    public String toString()
    {
        return "WH:" + getName() + "(" + id + ")";
    }
}
