package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.requests.restaction.MemberAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class MemberActionImpl extends RestActionImpl<Void> implements MemberAction {
    private final String accessToken;
    private final String userId;
    private final Guild guild;

    private String nick;
    private Set<Role> roles;
    private boolean mute;
    private boolean deaf;

    public MemberActionImpl(JDA api, Guild guild, String userId, String accessToken) {
        super(api, Route.Guilds.ADD_MEMBER.compile(guild.getId(), userId));
        this.accessToken = accessToken;
        this.userId = userId;
        this.guild = guild;
    }

    @Nonnull
    @Override
    public MemberAction setCheck(BooleanSupplier checks) {
        return (MemberAction) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Nonnull
    @Override
    public String getUserId() {
        return userId;
    }

    @Nullable
    @Override
    public User getUser() {
        return getJDA().getUserById(userId);
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return guild;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MemberActionImpl setNickname(String nick) {
        if (nick != null) {
            if (Helpers.isBlank(nick)) {
                this.nick = null;
                return this;
            }
            Checks.check(nick.length() <= 32, "Nickname must not be greater than 32 characters in length");
        }
        this.nick = nick;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MemberActionImpl setRoles(Collection<Role> roles) {
        if (roles == null) {
            this.roles = null;
            return this;
        }
        Set<Role> newRoles = new HashSet<>(roles.size());
        for (Role role : roles)
            checkAndAdd(newRoles, role);
        this.roles = newRoles;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MemberActionImpl setRoles(Role... roles) {
        if (roles == null) {
            this.roles = null;
            return this;
        }
        Set<Role> newRoles = new HashSet<>(roles.length);
        for (Role role : roles)
            checkAndAdd(newRoles, role);
        this.roles = newRoles;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MemberActionImpl setMute(boolean mute) {
        this.mute = mute;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MemberActionImpl setDeafen(boolean deaf) {
        this.deaf = deaf;
        return this;
    }

    @Override
    protected RequestBody finalizeData() {
        DataObject obj = DataObject.empty();
        obj.put("access_token", accessToken);
        if (nick != null)
            obj.put("nick", nick);
        if (roles != null && !roles.isEmpty())
            obj.put("roles", roles.stream().map(Role::getId).collect(Collectors.toList()));
        obj.put("mute", mute);
        obj.put("deaf", deaf);
        return getRequestBody(obj);
    }

    private void checkAndAdd(Set<Role> newRoles, Role role) {
        Checks.notNull(role, "Role");
        Checks.check(role.getGuild().equals(getGuild()), "Roles must all be from the same guild");
        newRoles.add(role);
    }
}
