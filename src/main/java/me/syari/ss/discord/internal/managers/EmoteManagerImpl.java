

package me.syari.ss.discord.internal.managers;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.managers.EmoteManager;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.EmoteImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EmoteManagerImpl extends ManagerBase<EmoteManager> implements EmoteManager
{
    protected final EmoteImpl emote;

    protected final List<String> roles = new ArrayList<>();
    protected String name;


    public EmoteManagerImpl(EmoteImpl emote)
    {
        super(emote.getJDA(), Route.Emotes.MODIFY_EMOTE.compile(notNullGuild(emote).getId(), emote.getId()));
        this.emote = emote;
        if (isPermissionChecksEnabled())
            checkPermissions();
    }

    private static Guild notNullGuild(EmoteImpl emote)
    {
        Guild g = emote.getGuild();
        if (g == null)
            throw new IllegalStateException("Cannot modify a fake emote");
        return g;
    }

    @Nonnull
    @Override
    public Emote getEmote()
    {
        return emote;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public EmoteManagerImpl reset(long fields)
    {
        super.reset(fields);
        if ((fields & ROLES) == ROLES)
            withLock(this.roles, List::clear);
        if ((fields & NAME) == NAME)
            this.name = null;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public EmoteManagerImpl reset(long... fields)
    {
        super.reset(fields);
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public EmoteManagerImpl reset()
    {
        super.reset();
        withLock(this.roles, List::clear);
        this.name = null;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public EmoteManagerImpl setName(@Nonnull String name)
    {
        Checks.notBlank(name, "Name");
        Checks.check(name.length() >= 2 && name.length() <= 32, "Name must be between 2-32 characters long");
        this.name = name;
        set |= NAME;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public EmoteManagerImpl setRoles(Set<Role> roles)
    {
        if (roles == null)
        {
            withLock(this.roles, List::clear);
        }
        else
        {
            Checks.notNull(roles, "Roles");
            roles.forEach((role) ->
            {
                Checks.notNull(role, "Roles");
                Checks.check(role.getGuild().equals(getGuild()), "Roles must all be from the same guild");
            });
            withLock(this.roles, (list) ->
            {
                list.clear();
                roles.stream().map(Role::getId).forEach(list::add);
            });
        }
        set |= ROLES;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();
        if (shouldUpdate(NAME))
            object.put("name", name);
        withLock(this.roles, (list) ->
        {
            if (shouldUpdate(ROLES))
                object.put("roles", DataArray.fromCollection(list));
        });
        reset();
        return getRequestBody(object);
    }

    @Override
    protected boolean checkPermissions()
    {
        if (!getGuild().getSelfMember().hasPermission(Permission.MANAGE_EMOTES))
            throw new InsufficientPermissionException(getGuild(), Permission.MANAGE_EMOTES);
        return super.checkPermissions();
    }
}
