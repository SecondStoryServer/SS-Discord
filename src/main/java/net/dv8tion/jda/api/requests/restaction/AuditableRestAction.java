

package net.dv8tion.jda.api.requests.restaction;

import net.dv8tion.jda.api.audit.ThreadLocalReason;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

/**
 * Extension of RestAction to allow setting a reason, only available to accounts of {@link net.dv8tion.jda.api.AccountType#BOT AccountType.BOT}
 *
 * <p>This will automatically use the {@link net.dv8tion.jda.api.audit.ThreadLocalReason ThreadLocalReason} if no
 * reason was specified via {@link #reason(String)}.
 *
 * @param  <T>
 *         The return type
 *
 * @since  3.3.0
 */
public interface AuditableRestAction<T> extends RestAction<T>
{
    /**
     * Applies the specified reason as audit-log reason field.
     * <br>When the provided reason is empty or {@code null} it will be treated as not set.
     *
     * <p>Reasons for any AuditableRestAction may be retrieved
     * via {@link net.dv8tion.jda.api.audit.AuditLogEntry#getReason() AuditLogEntry.getReason()}
     * in iterable {@link AuditLogPaginationAction AuditLogPaginationActions}
     * from {@link net.dv8tion.jda.api.entities.Guild#retrieveAuditLogs() Guild.retrieveAuditLogs()}!
     *
     * <p>This will specify the reason via the {@code X-Audit-Log-Reason} Request Header.
     * <br><b>Note: This may not be available to accounts for {@link net.dv8tion.jda.api.AccountType#CLIENT AccountType.CLIENT}.
     * <br>Using methods with a reason parameter will always work and <u>override</u> this header.</b>
     * (ct. {@link net.dv8tion.jda.api.entities.Guild#ban(net.dv8tion.jda.api.entities.User, int, String) Guild.ban(User, int, String)})
     *
     * @param  reason
     *         The reason for this action which should be logged in the Guild's AuditLogs
     *
     * @return The current AuditableRestAction instance for chaining convenience
     *
     * @see    ThreadLocalReason
     */
    @Nonnull
    AuditableRestAction<T> reason(@Nullable String reason);

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    AuditableRestAction<T> setCheck(@Nullable BooleanSupplier checks);
}
