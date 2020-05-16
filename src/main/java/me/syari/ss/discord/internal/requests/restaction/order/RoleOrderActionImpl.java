

package me.syari.ss.discord.internal.requests.restaction.order;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.restaction.order.RoleOrderAction;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleOrderActionImpl
    extends OrderActionImpl<Role, RoleOrderAction>
    implements RoleOrderAction
{
    protected final Guild guild;


    public RoleOrderActionImpl(Guild guild, boolean useAscendingOrder)
    {
        super(guild.getJDA(), !useAscendingOrder, Route.Guilds.MODIFY_ROLES.compile(guild.getId()));
        this.guild = guild;

        List<Role> roles = guild.getRoles();
        roles = roles.subList(0, roles.size() - 1); //Don't include the @everyone role.

        if (useAscendingOrder)
        {
            //Add roles to orderList in reverse due to role position ordering being descending
            // Top role starts at roles.size() - 1, bottom is 0.
            for (int i = roles.size() - 1; i >= 0; i--)
                this.orderList.add(roles.get(i));
        }
        else
        {
            //If not using discord ordering, we are ascending, so we add from first to last.
            // We add first to last because the roles provided from getRoles() are in ascending order already
            // with the highest role at index 0.
            this.orderList.addAll(roles);
        }

    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Override
    protected RequestBody finalizeData()
    {
        final Member self = guild.getSelfMember();
        final boolean isOwner = self.isOwner();

        if (!isOwner)
        {
            if (self.getRoles().isEmpty())
                throw new IllegalStateException("Cannot move roles above your highest role unless you are the guild owner");
            if (!self.hasPermission(Permission.MANAGE_ROLES))
                throw new InsufficientPermissionException(guild, Permission.MANAGE_ROLES);
        }

        DataArray array = DataArray.empty();
        List<Role> ordering = new ArrayList<>(orderList);

        //If not in normal discord order, reverse.
        // Normal order is descending, not ascending.
        if (ascendingOrder)
            Collections.reverse(ordering);

        for (int i = 0; i < ordering.size(); i++)
        {
            Role role = ordering.get(i);
            final int initialPos = role.getPosition();
            if (initialPos != i && !isOwner && !self.canInteract(role))
                // If the current role was moved, we are not owner and we can't interact with the role then throw a PermissionException
                throw new IllegalStateException("Cannot change order: One of the roles could not be moved due to hierarchical power!");

            array.add(DataObject.empty()
                    .put("id", role.getId())
                    .put("position", i + 1)); //plus 1 because position 0 is the @everyone position.
        }

        return getRequestBody(array);
    }
}

