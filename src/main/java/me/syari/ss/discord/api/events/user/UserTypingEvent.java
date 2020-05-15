
package me.syari.ss.discord.api.events.user;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;


public class UserTypingEvent extends GenericUserEvent
{
    private final MessageChannel channel;
    private final OffsetDateTime timestamp;

    public UserTypingEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageChannel channel, @Nonnull OffsetDateTime timestamp)
    {
        super(api, responseNumber, user);
        this.channel = channel;
        this.timestamp = timestamp;
    }


    @Nonnull
    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }


    @Nonnull
    public MessageChannel getChannel()
    {
        return channel;
    }


    public boolean isFromType(@Nonnull ChannelType type)
    {
        return channel.getType() == type;
    }


    @Nonnull
    public ChannelType getType()
    {
        return channel.getType();
    }


    @Nullable
    public PrivateChannel getPrivateChannel()
    {
        return isFromType(ChannelType.PRIVATE) ? (PrivateChannel) channel : null;
    }


    @Nullable
    public TextChannel getTextChannel()
    {
        return isFromType(ChannelType.TEXT) ? (TextChannel) channel : null;
    }


    @Nullable
    public Guild getGuild()
    {
        return isFromType(ChannelType.TEXT) ? getTextChannel().getGuild() : null;
    }


    @Nullable
    public Member getMember()
    {
        return isFromType(ChannelType.TEXT) ? getGuild().getMember(getUser()) : null;
    }
}
