

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.MessageActivity;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DataMessage extends AbstractMessage
{
    private MessageEmbed embed;

    public DataMessage(boolean tts, String content, String nonce, MessageEmbed embed)
    {
        super(content, nonce, tts);
        this.embed = embed;
    }

    @Nonnull
    @Override
    public MessageType getType()
    {
        return MessageType.DEFAULT;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof DataMessage))
            return false;
        DataMessage other = (DataMessage) o;
        return isTTS == other.isTTS
            && other.content.equals(content)
            && Objects.equals(other.nonce, nonce)
            && Objects.equals(other.embed, embed);
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    @Override
    public String toString()
    {
        return String.format("DataMessage(%.30s)", getContentRaw());
    }

    public DataMessage setEmbed(MessageEmbed embed)
    {
        this.embed = embed;
        return this;
    }

    @Nonnull
    @Override
    public List<MessageEmbed> getEmbeds()
    {
        return embed == null ? Collections.emptyList() : Collections.singletonList(embed);
    }

    // UNSUPPORTED OPERATIONS ON MESSAGE BUILDER OUTPUT

    @Override
    protected void unsupported()
    {
        throw new UnsupportedOperationException("This operation is not supported for Messages that were created by a MessageBuilder!");
    }

    @Nullable
    @Override
    public MessageActivity getActivity()
    {
        unsupported();
        return null;
    }

    @Override
    public long getIdLong()
    {
        unsupported();
        return 0;
    }
}
