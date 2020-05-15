
package net.dv8tion.jda.api.events.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a Message was edited in a {@link net.dv8tion.jda.api.entities.MessageChannel MessageChannel}.
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
     * The {@link net.dv8tion.jda.api.entities.Message Message} that was updated
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
     * @see    net.dv8tion.jda.api.entities.User User
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
