

package net.dv8tion.jda.api.events.message.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a MessageReaction was added/removed.
 * <br>Every MessageReactionEvent is derived from this event and can be casted.
 *
 * <p>Can be used to detect both remove and add events.
 */
public class GenericMessageReactionEvent extends GenericMessageEvent
{
    protected final long userId;
    protected User issuer;
    protected Member member;
    protected MessageReaction reaction;

    public GenericMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user,
                                       @Nullable Member member, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, reaction.getMessageIdLong(), reaction.getChannel());
        this.userId = userId;
        this.issuer = user;
        this.member = member;
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
     * @return The reacting user or null if this information is missing
     */
    @Nullable
    public User getUser()
    {
        return issuer;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.Member Member} instance for the reacting user
     * or {@code null} if the reaction was from a user not in this guild.
     *
     * @throws java.lang.IllegalStateException
     *         If this was not sent in a {@link net.dv8tion.jda.api.entities.TextChannel}.
     *
     * @return Member of the reacting user or null if they are no longer member of this guild
     *
     * @see    #isFromGuild()
     * @see    #getChannelType()
     */
    @Nullable
    public Member getMember()
    {
        return member;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.MessageReaction MessageReaction}
     *
     * @return The MessageReaction
     */
    @Nonnull
    public MessageReaction getReaction()
    {
        return reaction;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote ReactionEmote}
     * of the reaction, shortcut for {@code getReaction().getReactionEmote()}
     *
     * @return The ReactionEmote instance
     */
    @Nonnull
    public MessageReaction.ReactionEmote getReactionEmote()
    {
        return reaction.getReactionEmote();
    }
}
