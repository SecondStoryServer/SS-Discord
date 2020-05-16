package me.syari.ss.discord.api.audit;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.entities.UserImpl;
import me.syari.ss.discord.internal.entities.WebhookImpl;

import java.util.Collections;
import java.util.Map;


public class AuditLogEntry implements ISnowflake {
    protected final long id;
    protected final long targetId;
    protected final GuildImpl guild;
    protected final UserImpl user;
    protected final WebhookImpl webhook;
    protected final String reason;

    protected final Map<String, AuditLogChange> changes;
    protected final Map<String, Object> options;
    protected final ActionType type;

    public AuditLogEntry(ActionType type, long id, long targetId, GuildImpl guild, UserImpl user, WebhookImpl webhook,
                         String reason, Map<String, AuditLogChange> changes, Map<String, Object> options) {
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
    public long getIdLong() {
        return id;
    }


    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AuditLogEntry))
            return false;
        AuditLogEntry other = (AuditLogEntry) obj;
        return other.id == id && other.targetId == targetId;
    }

    @Override
    public String toString() {
        return "ALE:" + type + "(ID:" + id + " / TID:" + targetId + " / " + guild + ')';
    }

}
