

/**
 * Implementation for {@link net.dv8tion.jda.api.audit.AuditLogEntry AuditLogEntry}
 * and all utilities needed for its structure.
 * To retrieve entries use the {@link net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction AuditLogPaginationAction}
 * which can be retrieved from any {@link net.dv8tion.jda.api.entities.Guild Guild} instance
 * through {@link net.dv8tion.jda.api.entities.Guild#retrieveAuditLogs() Guild.retrieveAuditLogs()}.
 *
 * <p>Each Entry contains a set of {@link net.dv8tion.jda.api.audit.AuditLogChange AuditLogChanges}.
 * <br>To identify what kind of entry is represented use {@link net.dv8tion.jda.api.audit.ActionType ActionType}!
 *
 * @since  3.1.1
 */
package net.dv8tion.jda.api.audit;
