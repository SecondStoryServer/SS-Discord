package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.PermissionOverrideAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.PermissionOverrideImpl;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class PermissionOverrideActionImpl
        extends AuditableRestActionImpl<PermissionOverride>
        implements PermissionOverrideAction {
    private boolean isOverride = true;
    private boolean allowSet = false;
    private boolean denySet = false;

    private long allow = 0;
    private long deny = 0;
    private final GuildChannel channel;
    private final IPermissionHolder permissionHolder;

    public PermissionOverrideActionImpl(PermissionOverride override) {
        this(override.getJDA(), override.getChannel(), override.isRoleOverride() ? override.getRole() : override.getMember());
    }

    public PermissionOverrideActionImpl(JDA api, GuildChannel channel, IPermissionHolder permissionHolder) {
        super(api, Route.Channels.CREATE_PERM_OVERRIDE.compile(channel.getId(), permissionHolder.getId()));
        this.channel = channel;
        this.permissionHolder = permissionHolder;
    }

    // Whether to keep original value of the current override or not - by default we override the value
    public PermissionOverrideActionImpl setOverride(boolean override) {
        isOverride = override;
        return this;
    }

    @Override
    protected BooleanSupplier finalizeChecks() {
        return () -> {
            if (!getGuild().getSelfMember().hasPermission(channel, Permission.MANAGE_PERMISSIONS))
                throw new InsufficientPermissionException(channel, Permission.MANAGE_PERMISSIONS);
            return true;
        };
    }

    @Nonnull
    @Override
    public PermissionOverrideActionImpl setCheck(BooleanSupplier checks) {
        return (PermissionOverrideActionImpl) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public PermissionOverrideAction resetAllow() {
        allow = getCurrentAllow();
        allowSet = false;
        return this;
    }

    @Nonnull
    @Override
    public PermissionOverrideAction resetDeny() {
        deny = getCurrentDeny();
        denySet = false;
        return this;
    }

    @Nonnull
    @Override
    public GuildChannel getChannel() {
        return channel;
    }

    @Override
    public Role getRole() {
        return isRole() ? (Role) permissionHolder : null;
    }

    @Override
    public Member getMember() {
        return isMember() ? (Member) permissionHolder : null;
    }

    @Override
    public boolean isMember() {
        return permissionHolder instanceof Member;
    }

    @Override
    public boolean isRole() {
        return permissionHolder instanceof Role;
    }

    private long getCurrentAllow() {
        if (isOverride)
            return 0;
        PermissionOverride override = channel.getPermissionOverride(permissionHolder);
        return override == null ? 0 : override.getAllowedRaw();
    }

    private long getCurrentDeny() {
        if (isOverride)
            return 0;
        PermissionOverride override = channel.getPermissionOverride(permissionHolder);
        return override == null ? 0 : override.getDeniedRaw();
    }

    @Override
    protected RequestBody finalizeData() {
        DataObject object = DataObject.empty();
        object.put("type", isRole() ? "role" : "member");
        object.put("allow", allowSet ? allow : getCurrentAllow());
        object.put("deny", denySet ? deny : getCurrentDeny());
        reset();
        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(Response response, Request<PermissionOverride> request) {
        DataObject object = (DataObject) request.getRawBody();
        PermissionOverrideImpl override = new PermissionOverrideImpl(channel, permissionHolder);
        override.setAllow(object.getLong("allow"));
        override.setDeny(object.getLong("deny"));
        //((AbstractChannelImpl<?,?>) channel).getOverrideMap().put(id, override); This is added by the event later
        request.onSuccess(override);
    }
}
