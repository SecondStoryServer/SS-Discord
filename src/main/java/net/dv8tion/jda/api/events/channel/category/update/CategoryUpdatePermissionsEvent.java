

package net.dv8tion.jda.api.events.channel.category.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Indicates that the permissions of a {@link net.dv8tion.jda.api.entities.Category Category} were updated.
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
     * List of all affected {@link net.dv8tion.jda.api.entities.IPermissionHolder IPermissionHolders}
     *
     * @return Immutable list of permission holders affected by this event
     */
    @Nonnull
    public List<IPermissionHolder> getChangedPermissionHolders()
    {
        return changed;
    }

    /**
     * Filtered list of affected {@link net.dv8tion.jda.api.entities.Role Roles}
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
     * Filtered list of affected {@link net.dv8tion.jda.api.entities.Member Members}
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
