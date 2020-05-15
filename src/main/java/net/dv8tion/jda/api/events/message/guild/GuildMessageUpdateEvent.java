
package net.dv8tion.jda.api.events.message.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a Message was edited in a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}.
 * 
 * <p>Can be used to retrieve affected TextChannel and Message.
 */
public class GuildMessageUpdateEvent extends GenericGuildMessageEvent
{
    private final Message message;

    public GuildMessageUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getTextChannel());
        this.message = message;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.Message Message}
     *
     * @return The Message
     */
    @Nonnull
    public Message getMessage()
    {
        return message;
    }

    /**
     * The author of this message
     *
     * @return The author of this message
     *
     * @see    net.dv8tion.jda.api.entities.User User
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.Member Member} instance of the author
     *
     * @return The member instance for the author
     */
    @Nullable
    public Member getMember()
    {
        return message.getMember();
    }
}
