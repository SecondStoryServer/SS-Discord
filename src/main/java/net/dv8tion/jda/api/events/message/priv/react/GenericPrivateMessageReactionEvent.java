

package net.dv8tion.jda.api.events.message.priv.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.GenericPrivateMessageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.MessageReaction MessageReaction} was added or removed.
 *
 * <p>Can be used to detect when a message reaction is added or removed from a message.
 */
public class GenericPrivateMessageReactionEvent extends GenericPrivateMessageEvent
{
    protected final long userId;
    protected final User issuer;
    protected final MessageReaction reaction;

    public GenericPrivateMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, reaction.getMessageIdLong(), (PrivateChannel) reaction.getChannel());
        this.userId = userId;
        this.issuer = user;
        this.reaction = reaction;
    }

    /**
     * The id for the user who added/removed their reaction.
     *
     * @return The user id
     */
    @Nonnull
    public String getUserId()
    {
        return Long.toUnsignedString(userId);
    }

    /**
     * The id for the user who added/removed their reaction.
     *
     * @return The user id
     */
    public long getUserIdLong()
    {
        return userId;
    }

    /**
     * The reacting {@link net.dv8tion.jda.api.entities.User User}
     * <br>This might be missing if the user was not cached.
     *
     * @return The reacting user
     */
    @Nullable
    public User getUser()
    {
        return issuer;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.MessageReaction MessageReaction}
     *
     * @return The message reaction
     */
    @Nonnull
    public MessageReaction getReaction()
    {
        return reaction;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote ReactionEmote}
     * <br>Shortcut for {@code getReaction().getReactionEmote()}
     *
     * @return The message reaction emote
     */
    @Nonnull
    public MessageReaction.ReactionEmote getReactionEmote()
    {
        return reaction.getReactionEmote();
    }
}
