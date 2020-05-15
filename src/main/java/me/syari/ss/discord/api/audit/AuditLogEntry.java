

package me.syari.ss.discord.api.audit;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.requests.restaction.pagination.AuditLogPaginationAction;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.entities.UserImpl;
import me.syari.ss.discord.internal.entities.WebhookImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.entities.Webhook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class AuditLogEntry implements ISnowflake
{
    protected final long id;
    protected final long targetId;
    protected final GuildImpl guild;
    protected final UserImpl user;
    protected final WebhookImpl webhook;
    protected final String reason;

    protected final Map<String, AuditLogChange> changes;
    protected final Map<String, Object> options;
    protected final ActionType type;
    protected final int rawType;

    public AuditLogEntry(ActionType type, int rawType, long id, long targetId, GuildImpl guild, UserImpl user, WebhookImpl webhook,
                         String reason, Map<String, AuditLogChange> changes, Map<String, Object> options)
    {
        this.rawType = rawType;
        this.type = type;
        this.id = id;
        this.targetId = targetId;
        this.guild = guild;
        this.user = user;
        this.webhook = webhook;
        this.reason = reason;
        this.changes = changes != null && !changes.isEmpty()
                ? Collections.unmodifiableMap(changes)
                : Collections.emptyMap();
        this.options = options != null && !options.isEmpty()
                ? Collections.unmodifiableMap(options)
                : Collections.emptyMap();
    }

    @Override
    public long getIdLong()
    {
        return id;
    }


    public long getTargetIdLong()
    {
        return targetId;
    }


    @Nonnull
    public String getTargetId()
    {
        return Long.toUnsignedString(targetId);
    }


    @Nullable
    public Webhook getWebhook()
    {
        return webhook;
    }


    @Nonnull
    public Guild getGuild()
    {
        return guild;
    }


    @Nullable
    public User getUser()
    {
        return user;
    }


    @Nullable
    public String getReason()
    {
        return reason;
    }


    @Nonnull
    public JDA getJDA()
    {
        return guild.getJDA();
    }


    @Nonnull
    public Map<String, AuditLogChange> getChanges()
    {
        return changes;
    }


    @Nullable
    public AuditLogChange getChangeByKey(@Nullable final AuditLogKey key)
    {
        return key == null ? null : getChangeByKey(key.getKey());
    }


    @Nullable
    public AuditLogChange getChangeByKey(@Nullable final String key)
    {
        return changes.get(key);
    }


    @Nonnull
    public List<AuditLogChange> getChangesForKeys(@Nonnull AuditLogKey... keys)
    {
        Checks.notNull(keys, "Keys");
        List<AuditLogChange> changes = new ArrayList<>(keys.length);
        for (AuditLogKey key : keys)
        {
            AuditLogChange change = getChangeByKey(key);
            if (change != null)
                changes.add(change);
        }
        return Collections.unmodifiableList(changes);
    }


    @Nonnull
    public Map<String, Object> getOptions()
    {
        return options;
    }


    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getOptionByName(@Nullable String name)
    {
        return (T) options.get(name);
    }


    @Nullable
    public <T> T getOption(@Nonnull AuditLogOption option)
    {
        Checks.notNull(option, "Option");
        return getOptionByName(option.getKey());
    }


    @Nonnull
    public List<Object> getOptions(@Nonnull AuditLogOption... options)
    {
        Checks.notNull(options, "Options");
        List<Object> items = new ArrayList<>(options.length);
        for (AuditLogOption option : options)
        {
            Object obj = getOption(option);
            if (obj != null)
                items.add(obj);
        }
        return Collections.unmodifiableList(items);
    }


    @Nonnull
    public ActionType getType()
    {
        return type;
    }


    public int getTypeRaw()
    {
        return rawType;
    }


    @Nonnull
    public TargetType getTargetType()
    {
        return type.getTargetType();
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AuditLogEntry))
            return false;
        AuditLogEntry other = (AuditLogEntry) obj;
        return other.id == id && other.targetId == targetId;
    }

    @Override
    public String toString()
    {
        return "ALE:" + type + "(ID:" + id + " / TID:" + targetId + " / " + guild + ')';
    }

}
