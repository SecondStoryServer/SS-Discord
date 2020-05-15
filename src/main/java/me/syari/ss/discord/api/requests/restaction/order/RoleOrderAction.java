

package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public interface RoleOrderAction extends OrderAction<Role, RoleOrderAction>
{

    @Nonnull
    Guild getGuild();
}
