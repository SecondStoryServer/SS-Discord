

package net.dv8tion.jda.api.requests.restaction.pagination;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link PaginationAction PaginationAction} that paginates the audit logs endpoint.
 * <br>Note that this implementation is not considered thread-safe as modifications to the cache are not done
 * with a lock. Calling methods on this class from multiple threads is not recommended.
 *
 * <p><b>Must provide not-null {@link net.dv8tion.jda.api.entities.Guild Guild} to compile a valid guild audit logs
 * pagination route</b>
 *
 * <h2>Limits</h2>
 * Minimum - 1
 * <br>Maximum - 100
 *
 * <h1>Example</h1>
 * <pre><code>
 * public class Listener extends ListenerAdapter
 * {
 *     {@literal @Override}
 *     public void onRoleCreate(RoleCreateEvent event)
 *     {
 *         {@literal List<TextChannel>} channels = event.getGuild().getTextChannelsByName("logs", true);
 *         if (channels.isEmpty()) return; // no log channel
 *         TextChannel channel = channels.get(0); // get first match
 *
 *         AuditLogPaginationAction auditLogs = event.getGuild().retrieveAuditLogs();
 *         auditLogs.type(ActionType.ROLE_CREATE); // only take ROLE_CREATE type
 *         auditLogs.limit(1); // take first
 *         auditLogs.queue( (entries) {@literal ->}
 *         {
 *             // callback has a list, this may be empty due to race conditions
 *             if (entries.isEmpty()) return;
 *             AuditLogEntry entry = entries.get(0);
 *             channel.sendMessageFormat("A role has been updated by %#s!", entry.getUser()).queue();
 *         });
 *     }
 * }
 * </code></pre>
 *
 * @since  3.2
 *
 * @see    Guild#retrieveAuditLogs()
 */
public interface AuditLogPaginationAction extends PaginationAction<AuditLogEntry, AuditLogPaginationAction>
{
    /**
     * The current target {@link net.dv8tion.jda.api.entities.Guild Guild} for
     * this AuditLogPaginationAction.
     *
     * @return The never-null target Guild
     */
    @Nonnull
    Guild getGuild();
    
    /**
     * Filters retrieved entities by the specified {@link net.dv8tion.jda.api.audit.ActionType ActionType}
     *
     * @param  type
     *         {@link net.dv8tion.jda.api.audit.ActionType ActionType} used to filter,
     *         or {@code null} to remove type filtering
     *
     * @return The current AuditLogPaginationAction for chaining convenience
     */
    @Nonnull
    AuditLogPaginationAction type(@Nullable ActionType type);

    /**
     * Filters retrieved entities by the specified {@link net.dv8tion.jda.api.entities.User User}.
     * <br>This specified the action issuer and not the target of an action. (Targets need not be users)
     *
     * @param  user
     *         {@link net.dv8tion.jda.api.entities.User User} used to filter,
     *         or {@code null} to remove user filtering
     *
     * @return The current AuditLogPaginationAction for chaining convenience
     */
    @Nonnull
    AuditLogPaginationAction user(@Nullable User user);

    /**
     * Filters retrieved entities by the specified {@link net.dv8tion.jda.api.entities.User User} id.
     * <br>This specified the action issuer and not the target of an action. (Targets need not be users)
     *
     * @param  userId
     *         {@link net.dv8tion.jda.api.entities.User User} id used to filter,
     *         or {@code null} to remove user filtering
     *
     * @throws IllegalArgumentException
     *         If the provided userId is not valid
     *
     * @return The current AuditLogPaginationAction for chaining convenience
     */
    @Nonnull
    AuditLogPaginationAction user(@Nullable String userId);

    /**
     * Filters retrieved entities by the specified {@link net.dv8tion.jda.api.entities.User User} id.
     *
     * @param  userId
     *         {@link net.dv8tion.jda.api.entities.User User} id used to filter,
     *         or {@code null} to remove user filtering
     *
     * @return The current AuditLogPaginationAction for chaining convenience
     */
    @Nonnull
    AuditLogPaginationAction user(long userId);
}
