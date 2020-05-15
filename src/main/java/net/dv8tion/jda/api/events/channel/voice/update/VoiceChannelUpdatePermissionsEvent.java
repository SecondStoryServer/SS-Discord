
package net.dv8tion.jda.api.events.channel.voice.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel}'s permission overrides changed.
 *
 * <p>Can be used to get affected VoiceChannel, affected Guild and affected {@link net.dv8tion.jda.api.entities.Role Roles}/{@link net.dv8tion.jda.api.entities.User Users}.
 */
public class VoiceChannelUpdatePermissionsEvent extends GenericVoiceChannelEvent
{
    private final List<IPermissionHolder> changedPermHolders;

    public VoiceChannelUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, @Nonnull List<IPermissionHolder> changed)
    {
        super(api, responseNumber, channel);
        this.changedPermHolders = changed;
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
        return changedPermHolders;
    }

    /**
     * List of affected {@link net.dv8tion.jda.api.entities.Role Roles}
     *
     * @return List of affected roles
     */
    @Nonnull
    public List<Role> getChangedRoles()
    {
        return changedPermHolders.stream()
                .filter(p -> p instanceof Role)
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
        return changedPermHolders.stream()
                .filter(p -> p instanceof Member)
                .map(Member.class::cast)
                .collect(Collectors.toList());
    }
}
