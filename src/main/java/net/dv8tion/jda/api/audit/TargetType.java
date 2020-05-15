

package net.dv8tion.jda.api.audit;

/**
 * TargetType for an {@link net.dv8tion.jda.api.audit.ActionType ActionType}
 * <br>This describes what kind of Discord entity is being targeted by an auditable action!
 *
 * <p>This can be found via {@link net.dv8tion.jda.api.audit.ActionType#getTargetType() ActionType.getTargetType()}
 * or {@link net.dv8tion.jda.api.audit.AuditLogEntry#getTargetType() AuditLogEntry.getTargetType()}.
 * <br>This helps to decide what entity type the target id of an AuditLogEntry refers to.
 *
 * <h2>Example</h2>
 * If {@code entry.getTargetType()} is type {@link #GUILD}
 * <br>Then the target id returned by {@code entry.getTargetId()} and {@code entry.getTargetIdLong()}
 * can be used with {@link net.dv8tion.jda.api.JDA#getGuildById(long) JDA.getGuildById(id)}
 */
public enum TargetType
{
    GUILD,
    CHANNEL,
    ROLE,
    MEMBER,
    INVITE,
    WEBHOOK,
    EMOTE,
    INTEGRATION,
    UNKNOWN
}
