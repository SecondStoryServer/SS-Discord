

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.utils.AttachmentOption;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PrivateChannelImpl implements PrivateChannel
{
    private final long id;
    private final User user;
    private long lastMessageId;
    private boolean fake = false;

    public PrivateChannelImpl(long id, User user)
    {
        this.id = id;
        this.user = user;
    }

    @Nonnull
    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public long getLatestMessageIdLong()
    {
        final long messageId = lastMessageId;
        if (messageId < 0)
            throw new IllegalStateException("No last message id found.");
        return messageId;
    }

    @Override
    public boolean hasLatestMessage()
    {
        return lastMessageId > 0;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return getUser().getName();
    }

    @Nonnull
    @Override
    public ChannelType getType()
    {
        return ChannelType.PRIVATE;
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return user.getJDA();
    }

    @Nonnull
    @Override
    public RestAction<Void> close()
    {
        Route.CompiledRoute route = Route.Channels.DELETE_CHANNEL.compile(getId());
        return new RestActionImpl<>(getJDA(), route);
    }

    @Nonnull
    @Override
    public List<CompletableFuture<Void>> purgeMessages(@Nonnull List<? extends Message> messages)
    {
        if (messages.isEmpty())
            return Collections.emptyList();
        for (Message m : messages)
        {
            if (m.getAuthor().equals(getJDA().getSelfUser()))
                continue;
            throw new IllegalArgumentException("Cannot delete messages of other users in a private channel");
        }
        return PrivateChannel.super.purgeMessages(messages);
    }

    @Override
    public long getIdLong()
    {
        return id;
    }

    @Override
    public boolean isFake()
    {
        return fake;
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull CharSequence text)
    {
        checkBot();
        return PrivateChannel.super.sendMessage(text);
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull MessageEmbed embed)
    {
        checkBot();
        return PrivateChannel.super.sendMessage(embed);
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull Message msg)
    {
        checkBot();
        return PrivateChannel.super.sendMessage(msg);
    }

    @Nonnull
    @Override
    public MessageAction sendFile(@Nonnull InputStream data, @Nonnull String fileName, @Nonnull AttachmentOption... options)
    {
        checkBot();
        return PrivateChannel.super.sendFile(data, fileName, options);
    }

    public PrivateChannelImpl setFake(boolean fake)
    {
        this.fake = fake;
        return this;
    }

    public PrivateChannelImpl setLastMessageId(long id)
    {
        this.lastMessageId = id;
        return this;
    }

    // -- Object --


    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof PrivateChannelImpl))
            return false;
        PrivateChannelImpl impl = (PrivateChannelImpl) obj;
        return impl.id == this.id;
    }

    @Override
    public String toString()
    {
        return "PC:" + getUser().getName() + '(' + getId() + ')';
    }

    private void checkBot()
    {
        if (getUser().isBot() && getJDA().getAccountType() == AccountType.BOT)
            throw new UnsupportedOperationException("Cannot send a private message between bots.");
    }
}
