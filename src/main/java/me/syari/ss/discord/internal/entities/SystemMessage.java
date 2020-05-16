package me.syari.ss.discord.internal.entities;

import gnu.trove.set.TLongSet;
import me.syari.ss.discord.api.entities.*;

import java.util.List;

public class SystemMessage extends ReceivedMessage {
    public SystemMessage(
            long id, MessageChannel channel, MessageType type,
            boolean fromWebhook, boolean mentionsEveryone, TLongSet mentionedUsers, TLongSet mentionedRoles,
            boolean tts,
            String content, String nonce, User author, Member member,
            List<MessageEmbed> embeds) {
        super(id, channel, type, fromWebhook, mentionsEveryone, mentionedUsers, mentionedRoles,
                tts, content, author, member, embeds);
    }

    @Override
    public String toString() {
        return "M:[" + type + ']' + author + '(' + id + ')';
    }
}
