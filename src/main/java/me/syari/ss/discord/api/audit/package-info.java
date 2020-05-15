

/**
 * Implementation for {@link me.syari.ss.discord.api.audit.AuditLogEntry AuditLogEntry}
 * and all utilities needed for its structure.
 * To retrieve entries use the {@link me.syari.ss.discord.api.requests.restaction.pagination.AuditLogPaginationAction AuditLogPaginationAction}
 * which can be retrieved from any {@link me.syari.ss.discord.api.entities.Guild Guild} instance
 * through {@link me.syari.ss.discord.api.entities.Guild#retrieveAuditLogs() Guild.retrieveAuditLogs()}.
 *
 * <p>Each Entry contains a set of {@link me.syari.ss.discord.api.audit.AuditLogChange AuditLogChanges}.
 * <br>To identify what kind of entry is represented use {@link me.syari.ss.discord.api.audit.ActionType ActionType}!
 *
 * @since  3.1.1
 */
package me.syari.ss.discord.api.audit;
