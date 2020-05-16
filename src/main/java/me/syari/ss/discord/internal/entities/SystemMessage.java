

package me.syari.ss.discord.internal.entities;

import gnu.trove.set.TLongSet;
import me.syari.ss.discord.api.entities.*;

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

    @Override
    public String toString()
    {
        return "M:[" + type + ']' + author + '(' + id + ')';
    }
}
