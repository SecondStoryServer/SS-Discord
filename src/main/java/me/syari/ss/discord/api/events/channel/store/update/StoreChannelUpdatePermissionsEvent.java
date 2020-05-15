

package me.syari.ss.discord.api.events.channel.store.update;

import me.syari.ss.discord.api.events.channel.store.GenericStoreChannelEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.IPermissionHolder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.StoreChannel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;


public class StoreChannelUpdatePermissionsEvent extends GenericStoreChannelEvent
{
    private final List<IPermissionHolder> changed;

    public StoreChannelUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel, List<IPermissionHolder> permHolders)
    {
        super(api, responseNumber, channel);
        this.changed = permHolders;
    }


    @Nonnull
    public List<IPermissionHolder> getChangedPermissionHolders()
    {
        return changed;
    }


    @Nonnull
    public List<Role> getChangedRoles()
    {
        return changed.stream()
            .filter(it -> it instanceof Role)
            .map(Role.class::cast)
            .collect(Collectors.toList());
    }


    @Nonnull
    public List<Member> getChangedMembers()
    {
        return changed.stream()
            .filter(it -> it instanceof Member)
            .map(Member.class::cast)
            .collect(Collectors.toList());
    }
}
