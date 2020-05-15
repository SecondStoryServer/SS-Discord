

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.audit.AuditLogEntry;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.audit.ThreadLocalReason;
import me.syari.ss.discord.api.requests.restaction.pagination.AuditLogPaginationAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;


public interface AuditableRestAction<T> extends RestAction<T>
{

    @Nonnull
    AuditableRestAction<T> reason(@Nullable String reason);


    @Nonnull
    @Override
    AuditableRestAction<T> setCheck(@Nullable BooleanSupplier checks);
}
