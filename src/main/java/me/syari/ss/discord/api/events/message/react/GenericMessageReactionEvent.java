

package me.syari.ss.discord.api.events.message.react;

import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.message.GenericMessageEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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

    
    @Nonnull
    public String getUserId()
    {
        return Long.toUnsignedString(userId);
    }

    
    public long getUserIdLong()
    {
        return userId;
    }

    
    @Nullable
    public User getUser()
    {
        return issuer;
    }

    
    @Nullable
    public Member getMember()
    {
        return member;
    }

    
    @Nonnull
    public MessageReaction getReaction()
    {
        return reaction;
    }

    
    @Nonnull
    public MessageReaction.ReactionEmote getReactionEmote()
    {
        return reaction.getReactionEmote();
    }
}
