
package net.dv8tion.jda.api.events.channel.text.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}'s permission overrides changed.
 *
 * <p>Can be use to detect when a TextChannel's permission overrides change and get affected {@link net.dv8tion.jda.api.entities.Role Roles}/{@link net.dv8tion.jda.api.entities.User Users}.
 */
public class TextChannelUpdatePermissionsEvent extends GenericTextChannelEvent
{
    private final List<IPermissionHolder> changed;

    public TextChannelUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nonnull List<IPermissionHolder> permHolders)
    {
        super(api, responseNumber, channel);
        this.changed = permHolders;
    }

    /**
     * The affected {@link net.dv8tion.jda.api.entities.IPermissionHolder IPermissionHolders}
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
     * List of affected {@link net.dv8tion.jda.api.entities.Role Roles}
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
     * List of affected {@link net.dv8tion.jda.api.entities.Member Members}
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
