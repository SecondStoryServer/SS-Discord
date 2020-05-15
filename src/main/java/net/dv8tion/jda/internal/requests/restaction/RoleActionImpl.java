

package net.dv8tion.jda.internal.requests.restaction;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class RoleActionImpl extends AuditableRestActionImpl<Role> implements RoleAction
{
    protected final Guild guild;
    protected Long permissions;
    protected String name = null;
    protected Integer color = null;
    protected Boolean hoisted = null;
    protected Boolean mentionable = null;

    /**
     * Creates a new RoleAction instance
     *
     * @param  guild
     *         The {@link net.dv8tion.jda.api.entities.Guild Guild} for which the Role should be created.
     */
    public RoleActionImpl(Guild guild)
    {
        super(guild.getJDA(), Route.Roles.CREATE_ROLE.compile(guild.getId()));
        this.guild = guild;
    }

    @Nonnull
    @Override
    public RoleActionImpl setCheck(BooleanSupplier checks)
    {
        return (RoleActionImpl) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public RoleActionImpl setName(String name)
    {
        Checks.check(name == null || name.length() > 0 && name.length() <= 100, "Name must be between 1-100 characters long");
        this.name = name;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public RoleActionImpl setHoisted(Boolean hoisted)
    {
        this.hoisted = hoisted;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public RoleActionImpl setMentionable(Boolean mentionable)
    {
        this.mentionable = mentionable;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public RoleActionImpl setColor(Integer rgb)
    {
        this.color = rgb;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public RoleActionImpl setPermissions(Long permissions)
    {
        if (permissions != null)
        {
            Checks.notNegative(permissions, "Raw Permissions");
            Checks.check(permissions <= Permission.ALL_PERMISSIONS, "Provided permissions may not be greater than a full permission set!");
            for (Permission p : Permission.getPermissions(permissions))
                checkPermission(p);
        }
        this.permissions = permissions;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();
        if (name != null)
            object.put("name", name);
        if (color != null)
            object.put("color", color & 0xFFFFFF);
        if (permissions != null)
            object.put("permissions", permissions);
        if (hoisted != null)
            object.put("hoist", hoisted);
        if (mentionable != null)
            object.put("mentionable", mentionable);

        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(Response response, Request<Role> request)
    {
        request.onSuccess(api.getEntityBuilder().createRole((GuildImpl) guild, response.getObject(), guild.getIdLong()));
    }

    private void checkPermission(Permission permission)
    {
        if (!guild.getSelfMember().hasPermission(permission))
            throw new InsufficientPermissionException(guild, permission);
    }
}
