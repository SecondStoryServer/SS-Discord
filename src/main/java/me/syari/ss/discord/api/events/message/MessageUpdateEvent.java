
package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a Message was edited in a {@link MessageChannel MessageChannel}.
 * 
 * <p>Can be used to detect a Message is edited in either a private or guild channel. Providing a MessageChannel and Message.
 * <br>This also includes whether a message is being pinned.
 *
 * <p><b>JDA does not have a cache for messages and is not able to provide previous information due to limitations by the
 * Discord API!</b>
 */
public class MessageUpdateEvent extends GenericMessageEvent
{
    private final Message message;

    public MessageUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getChannel());
        this.message = message;
    }

    /**
     * The {@link Message Message} that was updated
     * <br>Note: Messages in JDA are not updated, they are immutable and will not change their state.
     *
     * @return The updated Message
     */
    @Nonnull
    public Message getMessage()
    {
        return message;
    }

    /**
     * The author of the Message.
     *
     * @return The message author
     *
     * @see    User User
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }

    /**
     * Member instance for the author of this message or {@code null} if this
     * was not in a Guild.
     *
     * @return The Member instance for the author or null
     */
    @Nullable
    public Member getMember()
    {
        return  message.getMember();
    }
}
