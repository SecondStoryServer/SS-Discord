
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.channel.text.GenericTextChannelEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.IPermissionHolder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;


public class TextChannelUpdatePermissionsEvent extends GenericTextChannelEvent
{
    private final List<IPermissionHolder> changed;

    public TextChannelUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nonnull List<IPermissionHolder> permHolders)
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
