
package me.syari.ss.discord.api.events.message.guild;

import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a Message is received in a {@link TextChannel TextChannel}.
 * 
 * <p>Can be used to retrieve the affected TextChannel and Message.
 */
public class GuildMessageReceivedEvent extends GenericGuildMessageEvent
{
    private final Message message;

    public GuildMessageReceivedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getTextChannel());
        this.message = message;
    }

    /**
     * The received {@link Message Message} object.
     *
     * @return The received {@link Message Message} object.
     */
    @Nonnull
    public Message getMessage()
    {
        return message;
    }

    /**
     * The Author of the Message received as {@link User User} object.
     * <br>This will be never-null but might be a fake User if Message was sent via Webhook
     *
     * @return The Author of the Message.
     *
     * @see    #isWebhookMessage()
     * @see    User#isFake()
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }

    /**
     * The Author of the Message received as {@link Member Member} object.
     * <br>This will be {@code null} in case of {@link #isWebhookMessage() isWebhookMessage()} returning {@code true}.
     *
     * @return The Author of the Message as Member object.
     *
     * @see    #isWebhookMessage()
     */
    @Nullable
    public Member getMember()
    {
        return message.getMember();
    }

    /**
     * Whether or not the Message received was sent via a Webhook.
     * <br>This is a shortcut for {@code getMessage().isWebhookMessage()}.
     *
     * @return Whether or not the Message was sent via Webhook
     */
    public boolean isWebhookMessage()
    {
        return getMessage().isWebhookMessage();
    }
}
