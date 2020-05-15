

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

/**
 * Indicates that a {@link StoreChannel StoreChannel}'s permission overrides changed.
 *
 * <p>Can be use to detect when a StoreChannel's permission overrides change and get affected {@link Role Roles}/{@link Member Members}.
 */
public class StoreChannelUpdatePermissionsEvent extends GenericStoreChannelEvent
{
    private final List<IPermissionHolder> changed;

    public StoreChannelUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel, List<IPermissionHolder> permHolders)
    {
        super(api, responseNumber, channel);
        this.changed = permHolders;
    }

    /**
     * The affected {@link IPermissionHolder IPermissionHolders}
     *
     * @return The affected permission holders
     *
     * @see    #getChangedRoles()
     * @see    #getChangedMembers()
     */
    @Nonnull
    public List<IPermissionHolder> getChangedPermissionHolders()
    {
        return changed;
    }

    /**
     * List of affected {@link Role Roles}
     *
     * @return List of affected roles
     */
    @Nonnull
    public List<Role> getChangedRoles()
    {
        return changed.stream()
            .filter(it -> it instanceof Role)
            .map(Role.class::cast)
            .collect(Collectors.toList());
    }

    /**
     * List of affected {@link Member Members}
     *
     * @return List of affected members
     */
    @Nonnull
    public List<Member> getChangedMembers()
    {
        return changed.stream()
            .filter(it -> it instanceof Member)
            .map(Member.class::cast)
            .collect(Collectors.toList());
    }
}
