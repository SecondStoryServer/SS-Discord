package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;


public interface PermissionOverrideAction extends AuditableRestAction<PermissionOverride> {
    @Nonnull
    @Override
    PermissionOverrideAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    default PermissionOverrideAction reset() {
        return resetAllow().resetDeny();
    }


    @Nonnull
    PermissionOverrideAction resetAllow();


    @Nonnull
    PermissionOverrideAction resetDeny();


    @Nonnull
    GuildChannel getChannel();


    @Nullable
    Role getRole();


    @Nullable
    Member getMember();


    @Nonnull
    default Guild getGuild() {
        return getChannel().getGuild();
    }


    boolean isMember();


    boolean isRole();


}
