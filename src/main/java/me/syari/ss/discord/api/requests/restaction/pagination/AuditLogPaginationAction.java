package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.audit.ActionType;
import me.syari.ss.discord.api.audit.AuditLogEntry;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface AuditLogPaginationAction extends PaginationAction<AuditLogEntry, AuditLogPaginationAction> {

    @Nonnull
    Guild getGuild();


    @Nonnull
    AuditLogPaginationAction type(@Nullable ActionType type);


    @Nonnull
    AuditLogPaginationAction user(@Nullable User user);


    @Nonnull
    AuditLogPaginationAction user(@Nullable String userId);


    @Nonnull
    AuditLogPaginationAction user(long userId);
}
