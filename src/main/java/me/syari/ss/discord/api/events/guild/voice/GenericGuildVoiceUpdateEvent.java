

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class GenericGuildVoiceUpdateEvent extends GenericGuildVoiceEvent implements GuildVoiceUpdateEvent
{
    protected final VoiceChannel joined, left;

    public GenericGuildVoiceUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable VoiceChannel left, @Nullable VoiceChannel joined)
    {
        super(api, responseNumber, member);
        this.left = left;
        this.joined = joined;
    }

    @Nullable
    @Override
    public VoiceChannel getChannelLeft()
    {
        return left;
    }

    @Nullable
    @Override
    public VoiceChannel getChannelJoined()
    {
        return joined;
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier()
    {
        return IDENTIFIER;
    }

    @Nonnull
    @Override
    public Member getEntity()
    {
        return getMember();
    }

    @Nullable
    @Override
    public VoiceChannel getOldValue()
    {
        return getChannelLeft();
    }

    @Nullable
    @Override
    public VoiceChannel getNewValue()
    {
        return getChannelJoined();
    }

    @Override
    public String toString()
    {
        return "MemberVoiceUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
