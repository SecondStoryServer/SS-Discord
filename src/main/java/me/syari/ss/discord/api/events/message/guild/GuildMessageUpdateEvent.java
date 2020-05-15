
package me.syari.ss.discord.api.events.message.guild;

import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a Message was edited in a {@link TextChannel TextChannel}.
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
     * The {@link Message Message}
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
     * @see    User User
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }

    /**
     * The {@link Member Member} instance of the author
     *
     * @return The member instance for the author
     */
    @Nullable
    public Member getMember()
    {
        return message.getMember();
    }
}
