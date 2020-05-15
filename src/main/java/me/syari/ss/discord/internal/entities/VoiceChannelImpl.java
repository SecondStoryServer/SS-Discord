

package me.syari.ss.discord.internal.entities;

import gnu.trove.map.TLongObjectMap;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoiceChannelImpl extends AbstractChannelImpl<VoiceChannel, VoiceChannelImpl> implements VoiceChannel
{
    private final TLongObjectMap<Member> connectedMembers = MiscUtil.newLongMap();
    private int userLimit;
    private int bitrate;

    public VoiceChannelImpl(long id, GuildImpl guild)
    {
        super(id, guild);
    }

    @Override
    public VoiceChannelImpl setPosition(int rawPosition)
    {
        getGuild().getVoiceChannelsView().clearCachedLists();
        return super.setPosition(rawPosition);
    }

    @Override
    public int getUserLimit()
    {
        return userLimit;
    }

    @Override
    public int getBitrate()
    {
        return bitrate;
    }

    @Nonnull
    @Override
    public ChannelType getType()
    {
        return ChannelType.VOICE;
    }

    @Nonnull
    @Override
    public List<Member> getMembers()
    {
        return Collections.unmodifiableList(new ArrayList<>(connectedMembers.valueCollection()));
    }

    @Override
    public int getPosition()
    {
        List<VoiceChannel> channels = getGuild().getVoiceChannels();
        for (int i = 0; i < channels.size(); i++)
        {
            if (equals(channels.get(i)))
                return i;
        }
        throw new AssertionError("Somehow when determining position we never found the VoiceChannel in the Guild's channels? wtf?");
    }

    @Nonnull
    @Override
    public ChannelAction<VoiceChannel> createCopy(@Nonnull Guild guild)
    {
        Checks.notNull(guild, "Guild");
        ChannelAction<VoiceChannel> action = guild.createVoiceChannel(name).setBitrate(bitrate).setUserlimit(userLimit);
        if (guild.equals(getGuild()))
        {
            Category parent = getParent();
            if (parent != null)
                action.setParent(parent);
            for (PermissionOverride o : overrides.valueCollection())
            {
                if (o.isMemberOverride())
                    action.addPermissionOverride(o.getMember(), o.getAllowedRaw(), o.getDeniedRaw());
                else
                    action.addPermissionOverride(o.getRole(), o.getAllowedRaw(), o.getDeniedRaw());
            }
        }
        return action;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof VoiceChannel))
            return false;
        VoiceChannel oVChannel = (VoiceChannel) o;
        return this == oVChannel || this.getIdLong() == oVChannel.getIdLong();
    }

    @Override
    public String toString()
    {
        return "VC:" + getName() + '(' + id + ')';
    }

    // -- Setters --

    public VoiceChannelImpl setUserLimit(int userLimit)
    {
        this.userLimit = userLimit;
        return this;
    }

    public VoiceChannelImpl setBitrate(int bitrate)
    {
        this.bitrate = bitrate;
        return this;
    }

    // -- Map Getters --

    public TLongObjectMap<Member> getConnectedMembersMap()
    {
        return connectedMembers;
    }
}
