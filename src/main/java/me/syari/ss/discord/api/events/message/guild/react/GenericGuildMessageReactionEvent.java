

package me.syari.ss.discord.api.events.message.guild.react;

import me.syari.ss.discord.api.events.message.guild.GenericGuildMessageEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link MessageReaction MessageReaction} was added or removed in a TextChannel.
 *
 * <p>Can be used to detect when a reaction is added or removed in a TextChannel.
 */
public abstract class GenericGuildMessageReactionEvent extends GenericGuildMessageEvent
{
    protected final long userId;
    protected final Member issuer;
    protected final MessageReaction reaction;

    public GenericGuildMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nullable Member user, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, reaction.getMessageIdLong(), (TextChannel) reaction.getChannel());
        this.issuer = user;
        this.reaction = reaction;
        this.userId = userId;
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
     * The reacting {@link User User}
     * <br>This might be missing if the user was not previously cached or the member was removed.
     *
     * @return The reacting user or null if this information is missing
     *
     * @see    #getUserIdLong()
     */
    @Nullable
    public User getUser()
    {
        return issuer == null ? getJDA().getUserById(userId) : issuer.getUser();
    }

    /**
     * The {@link Member Member} instance for the reacting user
     * <br>This might be missing if the user was not previously cached or the member was removed.
     *
     * @return The member instance for the reacting user or null if this information is missing
     */
    @Nullable
    public Member getMember()
    {
        return issuer;
    }

    /**
     * The {@link MessageReaction MessageReaction}
     *
     * @return The message reaction
     */
    @Nonnull
    public MessageReaction getReaction()
    {
        return reaction;
    }

    /**
     * The {@link MessageReaction.ReactionEmote ReactionEmote}
     * <br>Shortcut for {@code getReaction().getReactionEmote()}
     *
     * @return The reaction emote
     */
    @Nonnull
    public MessageReaction.ReactionEmote getReactionEmote()
    {
        return reaction.getReactionEmote();
    }
}
