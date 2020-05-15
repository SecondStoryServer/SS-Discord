

package me.syari.ss.discord.api.events.channel.category.update;

import me.syari.ss.discord.api.events.channel.category.GenericCategoryEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.entities.IPermissionHolder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Indicates that the permissions of a {@link Category Category} were updated.
 *
 * <p>Can be used to retrieve the changed permissions
 */
public class CategoryUpdatePermissionsEvent extends GenericCategoryEvent
{
    protected final List<IPermissionHolder> changed;

    public CategoryUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category, @Nonnull List<IPermissionHolder> changed)
    {
        super(api, responseNumber, category);
        this.changed = changed;
    }

    /**
     * List of all affected {@link IPermissionHolder IPermissionHolders}
     *
     * @return Immutable list of permission holders affected by this event
     */
    @Nonnull
    public List<IPermissionHolder> getChangedPermissionHolders()
    {
        return changed;
    }

    /**
     * Filtered list of affected {@link Role Roles}
     *
     * @return Immutable list of affected roles
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
     * Filtered list of affected {@link Member Members}
     *
     * @return Immutable list of affected members
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
