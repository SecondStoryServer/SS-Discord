

package me.syari.ss.discord.internal.requests.restaction.pagination;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.ParsingException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.pagination.AuditLogPaginationAction;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.audit.ActionType;
import me.syari.ss.discord.api.audit.AuditLogEntry;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AuditLogPaginationActionImpl
    extends PaginationActionImpl<AuditLogEntry, AuditLogPaginationAction>
    implements AuditLogPaginationAction
{
    protected final Guild guild;
    // filters
    protected ActionType type = null;
    protected String userId = null;

    public AuditLogPaginationActionImpl(Guild guild)
    {
        super(guild.getJDA(), Route.Guilds.GET_AUDIT_LOGS.compile(guild.getId()), 1, 100, 100);
        if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS))
            throw new InsufficientPermissionException(guild, Permission.VIEW_AUDIT_LOGS);
        this.guild = guild;
    }

    @Nonnull
    @Override
    public AuditLogPaginationActionImpl type(ActionType type)
    {
        this.type = type;
        return this;
    }

    @Nonnull
    @Override
    public AuditLogPaginationActionImpl user(User user)
    {
        return user(user == null ? null : user.getId());
    }

    @Nonnull
    @Override
    public AuditLogPaginationActionImpl user(String userId)
    {
        if (userId != null)
            Checks.isSnowflake(userId, "User ID");
        this.userId = userId;
        return this;
    }

    @Nonnull
    @Override
    public AuditLogPaginationActionImpl user(long userId)
    {
        return user(Long.toUnsignedString(userId));
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Override
    protected Route.CompiledRoute finalizeRoute()
    {
        Route.CompiledRoute route = super.finalizeRoute();

        final String limit = String.valueOf(this.limit.get());
        final long last = this.lastKey;

        route = route.withQueryParams("limit", limit);

        if (type != null)
            route = route.withQueryParams("action_type", String.valueOf(type.getKey()));

        if (userId != null)
            route = route.withQueryParams("user_id", userId);

        if (last != 0)
            route = route.withQueryParams("before", Long.toUnsignedString(last));

        return route;
    }

    @Override
    protected void handleSuccess(Response response, Request<List<AuditLogEntry>> request)
    {
        DataObject obj = response.getObject();
        DataArray users = obj.getArray("users");
        DataArray webhooks = obj.getArray("webhooks");
        DataArray entries = obj.getArray("audit_log_entries");

        List<AuditLogEntry> list = new ArrayList<>(entries.length());
        EntityBuilder builder = api.getEntityBuilder();

        TLongObjectMap<DataObject> userMap = new TLongObjectHashMap<>();
        for (int i = 0; i < users.length(); i++)
        {
            DataObject user = users.getObject(i);
            userMap.put(user.getLong("id"), user);
        }

        TLongObjectMap<DataObject> webhookMap = new TLongObjectHashMap<>();
        for (int i = 0; i < webhooks.length(); i++)
        {
            DataObject webhook = webhooks.getObject(i);
            webhookMap.put(webhook.getLong("id"), webhook);
        }

        for (int i = 0; i < entries.length(); i++)
        {
            try
            {
                DataObject entry = entries.getObject(i);
                DataObject user = userMap.get(entry.getLong("user_id", 0));
                DataObject webhook = webhookMap.get(entry.getLong("target_id", 0));
                AuditLogEntry result = builder.createAuditLogEntry((GuildImpl) guild, entry, user, webhook);
                list.add(result);
                if (this.useCache)
                    this.cached.add(result);
                this.last = result;
                this.lastKey = last.getIdLong();
            }
            catch (ParsingException | NullPointerException e)
            {
                RestActionImpl.LOG.warn("Encountered exception in AuditLogPagination", e);
            }
        }

        request.onSuccess(list);
    }

    @Override
    protected long getKey(AuditLogEntry it)
    {
        return it.getIdLong();
    }
}
