
package me.syari.ss.discord.api.events.user;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

/**
 * Indicates that a {@link User User} started typing. (Similar to the typing indicator in the Discord client)
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve the User who started typing and when and in which MessageChannel they started typing.
 */
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

    /**
     * The time when the user started typing
     *
     * @return The time when the typing started
     */
    @Nonnull
    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }

    /**
     * The channel where the typing was started
     *
     * @return The channel
     */
    @Nonnull
    public MessageChannel getChannel()
    {
        return channel;
    }

    /**
     * Whether the user started typing in a channel of the specified type.
     *
     * @param  type
     *         {@link ChannelType ChannelType}
     *
     * @return True, if the user started typing in a channel of the specified type
     */
    public boolean isFromType(@Nonnull ChannelType type)
    {
        return channel.getType() == type;
    }

    /**
     * The {@link ChannelType ChannelType}
     *
     * @return The {@link ChannelType ChannelType}
     */
    @Nonnull
    public ChannelType getType()
    {
        return channel.getType();
    }

    /**
     * {@link PrivateChannel PrivateChannel} in which this users started typing,
     * or {@code null} if this was not in a PrivateChannel.
     *
     * @return Possibly-null {@link PrivateChannel PrivateChannel}
     */
    @Nullable
    public PrivateChannel getPrivateChannel()
    {
        return isFromType(ChannelType.PRIVATE) ? (PrivateChannel) channel : null;
    }

    /**
     * {@link TextChannel TextChannel} in which this users started typing,
     * or {@code null} if this was not in a TextChannel.
     *
     * @return Possibly-null {@link TextChannel TextChannel}
     */
    @Nullable
    public TextChannel getTextChannel()
    {
        return isFromType(ChannelType.TEXT) ? (TextChannel) channel : null;
    }

    /**
     * {@link Guild Guild} in which this users started typing,
     * or {@code null} if this was not in a Guild.
     *
     * @return Possibly-null {@link Guild Guild}
     */
    @Nullable
    public Guild getGuild()
    {
        return isFromType(ChannelType.TEXT) ? getTextChannel().getGuild() : null;
    }

    /**
     * {@link Member Member} instance for the User, or null if this was not in a Guild.
     *
     * @return Possibly-null {@link Member Member}
     */
    @Nullable
    public Member getMember()
    {
        return isFromType(ChannelType.TEXT) ? getGuild().getMember(getUser()) : null;
    }
}
