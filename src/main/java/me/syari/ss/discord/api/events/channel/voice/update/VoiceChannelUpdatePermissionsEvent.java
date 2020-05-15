
package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.channel.voice.GenericVoiceChannelEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.IPermissionHolder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel}'s permission overrides changed.
 *
 * <p>Can be used to get affected VoiceChannel, affected Guild and affected {@link Role Roles}/{@link User Users}.
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
        return changedPermHolders;
    }

    /**
     * List of affected {@link Role Roles}
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
     * List of affected {@link Member Members}
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
