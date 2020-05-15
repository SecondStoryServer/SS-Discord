
package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.IPermissionHolder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.events.channel.voice.GenericVoiceChannelEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;


public class VoiceChannelUpdatePermissionsEvent extends GenericVoiceChannelEvent
{
    private final List<IPermissionHolder> changedPermHolders;

    public VoiceChannelUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, @Nonnull List<IPermissionHolder> changed)
    {
        super(api, responseNumber, channel);
        this.changedPermHolders = changed;
    }


    @Nonnull
    public List<IPermissionHolder> getChangedPermissionHolders()
    {
        return changedPermHolders;
    }


    @Nonnull
    public List<Role> getChangedRoles()
    {
        return changedPermHolders.stream()
                .filter(p -> p instanceof Role)
                .map(Role.class::cast)
                .collect(Collectors.toList());
    }


    @Nonnull
    public List<Member> getChangedMembers()
    {
        return changedPermHolders.stream()
                .filter(p -> p instanceof Member)
                .map(Member.class::cast)
                .collect(Collectors.toList());
    }
}
