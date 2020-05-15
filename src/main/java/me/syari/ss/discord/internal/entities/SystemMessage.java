

package me.syari.ss.discord.internal.entities;

import gnu.trove.set.TLongSet;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.MessageAction;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;

public class SystemMessage extends ReceivedMessage
{
    public SystemMessage(
            long id, MessageChannel channel, MessageType type,
            boolean fromWebhook, boolean mentionsEveryone, TLongSet mentionedUsers, TLongSet mentionedRoles,
            boolean tts, boolean pinned,
            String content, String nonce, User author, Member member, MessageActivity activity, OffsetDateTime editTime,
            List<MessageReaction> reactions, List<Attachment> attachments, List<MessageEmbed> embeds, int flags)
    {
        super(id, channel, type, fromWebhook, mentionsEveryone, mentionedUsers, mentionedRoles,
            tts, pinned, content, nonce, author, member, activity, editTime, reactions, attachments, embeds, flags);
    }

    @Nonnull
    @Override
    public RestAction<Void> pin()
    {
        throw new UnsupportedOperationException("Cannot pin message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public RestAction<Void> unpin()
    {
        throw new UnsupportedOperationException("Cannot unpin message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public RestAction<Void> addReaction(@Nonnull Emote emote)
    {
        throw new UnsupportedOperationException("Cannot add reactions to message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public RestAction<Void> addReaction(@Nonnull String unicode)
    {
        throw new UnsupportedOperationException("Cannot add reactions to message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public RestAction<Void> clearReactions()
    {
        throw new UnsupportedOperationException("Cannot clear reactions for message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public MessageAction editMessage(@Nonnull CharSequence newContent)
    {
        throw new UnsupportedOperationException("Cannot edit message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public MessageAction editMessage(@Nonnull MessageEmbed newContent)
    {
        throw new UnsupportedOperationException("Cannot edit message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public MessageAction editMessageFormat(@Nonnull String format, @Nonnull Object... args)
    {
        throw new UnsupportedOperationException("Cannot edit message of this Message Type. MessageType: " + getType());
    }

    @Nonnull
    @Override
    public MessageAction editMessage(@Nonnull Message newContent)
    {
        throw new UnsupportedOperationException("Cannot edit message of this Message Type. MessageType: " + getType());
    }

    @Override
    public String toString()
    {
        return "M:[" + type + ']' + author + '(' + id + ')';
    }
}
