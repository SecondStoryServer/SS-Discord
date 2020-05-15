
package me.syari.ss.discord.api.entities;

import gnu.trove.map.TLongObjectMap;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import me.syari.ss.discord.api.requests.restaction.PermissionOverrideAction;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.managers.ChannelManager;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.InviteAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.GuildImpl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface GuildChannel extends ISnowflake, Comparable<GuildChannel>
{

    @Nonnull
    ChannelType getType();


    @Nonnull
    String getName();


    @Nonnull
    Guild getGuild();


    @Nullable
    Category getParent();


    @Nonnull
    List<Member> getMembers();


    int getPosition();


    int getPositionRaw();


    @Nonnull
    JDA getJDA();


    @Nullable
    PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder permissionHolder);


    @Nonnull
    List<PermissionOverride> getPermissionOverrides();


    @Nonnull
    List<PermissionOverride> getMemberPermissionOverrides();


    @Nonnull
    List<PermissionOverride> getRolePermissionOverrides();


    @Nonnull
    @CheckReturnValue
    ChannelAction<? extends GuildChannel> createCopy(@Nonnull Guild guild);


    @Nonnull
    @CheckReturnValue
    default ChannelAction<? extends GuildChannel> createCopy()
    {
        return createCopy(getGuild());
    }


    @Nonnull
    ChannelManager getManager();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction createPermissionOverride(@Nonnull IPermissionHolder permissionHolder);


    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction putPermissionOverride(@Nonnull IPermissionHolder permissionHolder);


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction upsertPermissionOverride(@Nonnull IPermissionHolder permissionHolder)
    {
        if (!getGuild().getSelfMember().hasPermission(this, Permission.MANAGE_PERMISSIONS))
            throw new InsufficientPermissionException(this, Permission.MANAGE_PERMISSIONS);
        PermissionOverride override = getPermissionOverride(permissionHolder);
        if (override != null)
            return override.getManager();
        PermissionOverrideAction action = putPermissionOverride(permissionHolder);
        // Check if we have some information cached already
        TLongObjectMap<DataObject> cache = ((GuildImpl) getGuild()).getOverrideMap(permissionHolder.getIdLong());
        DataObject json = cache == null ? null : cache.get(getIdLong());
        if (json != null)
            action = action.setPermissions(json.getLong("allow"), json.getLong("deny"));
        return action;
    }


    @Nonnull
    @CheckReturnValue
    InviteAction createInvite();


    @Nonnull
    @CheckReturnValue
    RestAction<List<Invite>> retrieveInvites();
}
