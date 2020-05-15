
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.*;
import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.exceptions.VerificationLevelException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.exceptions.AccountTypeException;
import me.syari.ss.discord.api.requests.restaction.pagination.MessagePaginationAction;
import me.syari.ss.discord.api.requests.restaction.pagination.PaginationAction;
import me.syari.ss.discord.api.utils.AttachmentOption;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.AuditableRestActionImpl;
import me.syari.ss.discord.internal.requests.restaction.MessageActionImpl;
import me.syari.ss.discord.internal.requests.restaction.pagination.MessagePaginationActionImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.EncodingUtil;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


public interface MessageChannel extends ISnowflake, Formattable
{

    @Nonnull
    default String getLatestMessageId()
    {
        return Long.toUnsignedString(getLatestMessageIdLong());
    }


    @Nonnull
    default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull List<String> messageIds)
    {
        if (messageIds == null || messageIds.isEmpty())
            return Collections.emptyList();
        long[] ids = new long[messageIds.size()];
        for (int i = 0; i < ids.length; i++)
            ids[i] = MiscUtil.parseSnowflake(messageIds.get(i));
        return purgeMessagesById(ids);
    }


    @Nonnull
    default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull String... messageIds)
    {
        if (messageIds == null || messageIds.length == 0)
            return Collections.emptyList();
        return purgeMessagesById(Arrays.asList(messageIds));
    }


    @Nonnull
    default List<CompletableFuture<Void>> purgeMessages(@Nonnull Message... messages)
    {
        if (messages == null || messages.length == 0)
            return Collections.emptyList();
        return purgeMessages(Arrays.asList(messages));
    }


    @Nonnull
    default List<CompletableFuture<Void>> purgeMessages(@Nonnull List<? extends Message> messages)
    {
        if (messages == null || messages.isEmpty())
            return Collections.emptyList();
        long[] ids = new long[messages.size()];
        for (int i = 0; i < ids.length; i++)
            ids[i] = messages.get(i).getIdLong();
        return purgeMessagesById(ids);
    }


    @Nonnull
    default List<CompletableFuture<Void>> purgeMessagesById(@Nonnull long... messageIds)
    {
        if (messageIds == null || messageIds.length == 0)
            return Collections.emptyList();
        List<CompletableFuture<Void>> list = new ArrayList<>(messageIds.length);
        TreeSet<Long> sortedIds = new TreeSet<>(Comparator.reverseOrder());
        for (long messageId : messageIds)
            sortedIds.add(messageId);
        for (long messageId : sortedIds)
            list.add(deleteMessageById(messageId).submit());
        return list;
    }


    long getLatestMessageIdLong();


    boolean hasLatestMessage();


    @Nonnull
    String getName();


    @Nonnull
    ChannelType getType();


    @Nonnull
    JDA getJDA();


    @Nonnull
    @CheckReturnValue
    default MessageAction sendMessage(@Nonnull CharSequence text)
    {
        Checks.notEmpty(text, "Provided text for message");
        Checks.check(text.length() <= 2000, "Provided text for message must be less than 2000 characters in length");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        if (text instanceof StringBuilder)
            return new MessageActionImpl(getJDA(), route, this, (StringBuilder) text);
        else
            return new MessageActionImpl(getJDA(), route, this).append(text);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendMessageFormat(@Nonnull String format, @Nonnull Object... args)
    {
        Checks.notEmpty(format, "Format");
        return sendMessage(String.format(format, args));
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendMessage(@Nonnull MessageEmbed embed)
    {
        Checks.notNull(embed, "Provided embed");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        return new MessageActionImpl(getJDA(), route, this).embed(embed);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendMessage(@Nonnull Message msg)
    {
        Checks.notNull(msg, "Message");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        return new MessageActionImpl(getJDA(), route, this).apply(msg);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendFile(@Nonnull File file, @Nonnull AttachmentOption... options)
    {
        Checks.notNull(file, "file");

        return sendFile(file, file.getName(), options);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendFile(@Nonnull File file, @Nonnull String fileName, @Nonnull AttachmentOption... options)
    {
        Checks.notNull(file, "file");
        Checks.check(file.exists() && file.canRead(),
            "Provided file is either null, doesn't exist or is not readable!");
        Checks.check(file.length() <= getJDA().getSelfUser().getAllowedFileSize(),
            "File is too big! Max file-size is 8 MiB for normal and 50 MiB for nitro users");
        Checks.notNull(fileName, "fileName");

        try
        {
            return sendFile(new FileInputStream(file), fileName, options);
        }
        catch (FileNotFoundException ex)
        {
            throw new IllegalArgumentException(ex);
        }
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendFile(@Nonnull InputStream data, @Nonnull String fileName, @Nonnull AttachmentOption... options)
    {
        Checks.notNull(data, "data InputStream");
        Checks.notNull(fileName, "fileName");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        return new MessageActionImpl(getJDA(), route, this).addFile(data, fileName, options);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction sendFile(@Nonnull byte[] data, @Nonnull String fileName, @Nonnull AttachmentOption... options)
    {
        Checks.notNull(data, "data");
        Checks.notNull(fileName, "fileName");
        final long maxSize = getJDA().getSelfUser().getAllowedFileSize();
        Checks.check(data.length <= maxSize, "File is too big! Max file-size is %d bytes", maxSize);
        return sendFile(new ByteArrayInputStream(data), fileName, options);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Message> retrieveMessageById(@Nonnull String messageId)
    {
        AccountTypeException.check(getJDA().getAccountType(), AccountType.BOT);
        Checks.isSnowflake(messageId, "Message ID");

        JDAImpl jda = (JDAImpl) getJDA();
        Route.CompiledRoute route = Route.Messages.GET_MESSAGE.compile(getId(), messageId);
        return new RestActionImpl<>(jda, route,
            (response, request) -> jda.getEntityBuilder().createMessage(response.getObject(), MessageChannel.this, false));
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Message> retrieveMessageById(long messageId)
    {
        return retrieveMessageById(Long.toUnsignedString(messageId));
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> deleteMessageById(@Nonnull String messageId)
    {
        Checks.isSnowflake(messageId, "Message ID");

        Route.CompiledRoute route = Route.Messages.DELETE_MESSAGE.compile(getId(), messageId);
        return new AuditableRestActionImpl<>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> deleteMessageById(long messageId)
    {
        return deleteMessageById(Long.toUnsignedString(messageId));
    }


    default MessageHistory getHistory()
    {
        return new MessageHistory(this);
    }


    @Nonnull
    @CheckReturnValue
    default MessagePaginationAction getIterableHistory()
    {
        return new MessagePaginationActionImpl(this);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryAround(@Nonnull String messageId, int limit)
    {
        return MessageHistory.getHistoryAround(this, messageId).limit(limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryAround(long messageId, int limit)
    {
        return getHistoryAround(Long.toUnsignedString(messageId), limit );
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryAround(@Nonnull Message message, int limit)
    {
        Checks.notNull(message, "Provided target message");
        return getHistoryAround(message.getId(), limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryAfter(@Nonnull String messageId, int limit)
    {
        return MessageHistory.getHistoryAfter(this, messageId).limit(limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryAfter(long messageId, int limit)
    {
        return getHistoryAfter(Long.toUnsignedString(messageId), limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryAfter(@Nonnull Message message, int limit)
    {
        Checks.notNull(message, "Message");
        return getHistoryAfter(message.getId(), limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryBefore(@Nonnull String messageId, int limit)
    {
        return MessageHistory.getHistoryBefore(this, messageId).limit(limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryBefore(long messageId, int limit)
    {
        return getHistoryBefore(Long.toUnsignedString(messageId), limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryBefore(@Nonnull Message message, int limit)
    {
        Checks.notNull(message, "Message");
        return getHistoryBefore(message.getId(), limit);
    }


    @Nonnull
    @CheckReturnValue
    default MessageHistory.MessageRetrieveAction getHistoryFromBeginning(int limit)
    {
        return MessageHistory.getHistoryFromBeginning(this).limit(limit);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> sendTyping()
    {
        Route.CompiledRoute route = Route.Channels.SEND_TYPING.compile(getId());
        return new RestActionImpl<>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> addReactionById(@Nonnull String messageId, @Nonnull String unicode)
    {
        Checks.isSnowflake(messageId, "Message ID");
        Checks.notNull(unicode, "Provided Unicode");
        unicode = unicode.trim();
        Checks.notEmpty(unicode, "Provided Unicode");

        final String encoded = EncodingUtil.encodeReaction(unicode);

        Route.CompiledRoute route = Route.Messages.ADD_REACTION.compile(getId(), messageId, encoded, "@me");
        return new RestActionImpl<>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> addReactionById(long messageId, @Nonnull String unicode)
    {
        return addReactionById(Long.toUnsignedString(messageId), unicode);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> addReactionById(@Nonnull String messageId, @Nonnull Emote emote)
    {
        Checks.notNull(emote, "Emote");
        return addReactionById(messageId, emote.getName() + ":" + emote.getId());
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> addReactionById(long messageId, @Nonnull Emote emote)
    {
        return addReactionById(Long.toUnsignedString(messageId), emote);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull String unicode)
    {
        Checks.isSnowflake(messageId, "Message ID");
        Checks.notNull(unicode, "Provided Unicode");
        unicode = unicode.trim();
        Checks.notEmpty(unicode, "Provided Unicode");

        final String encoded = EncodingUtil.encodeReaction(unicode);

        final Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(getId(), messageId, encoded, "@me");
        return new RestActionImpl<>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(long messageId, @Nonnull String unicode)
    {
        return removeReactionById(Long.toUnsignedString(messageId), unicode);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull Emote emote)
    {
        Checks.notNull(emote, "Emote");
        return removeReactionById(messageId, emote.getName() + ":" + emote.getId());
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(long messageId, @Nonnull Emote emote)
    {
        return removeReactionById(Long.toUnsignedString(messageId), emote);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> pinMessageById(@Nonnull String messageId)
    {
        Checks.isSnowflake(messageId, "Message ID");

        Route.CompiledRoute route = Route.Messages.ADD_PINNED_MESSAGE.compile(getId(), messageId);
        return new RestActionImpl<Void>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> pinMessageById(long messageId)
    {
        return pinMessageById(Long.toUnsignedString(messageId));
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> unpinMessageById(@Nonnull String messageId)
    {
        Checks.isSnowflake(messageId, "Message ID");

        Route.CompiledRoute route = Route.Messages.REMOVE_PINNED_MESSAGE.compile(getId(), messageId);
        return new RestActionImpl<Void>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> unpinMessageById(long messageId)
    {
        return unpinMessageById(Long.toUnsignedString(messageId));
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<List<Message>> retrievePinnedMessages()
    {
        JDAImpl jda = (JDAImpl) getJDA();
        Route.CompiledRoute route = Route.Messages.GET_PINNED_MESSAGES.compile(getId());
        return new RestActionImpl<>(jda, route, (response, request) ->
        {
            LinkedList<Message> pinnedMessages = new LinkedList<>();
            EntityBuilder builder = jda.getEntityBuilder();
            DataArray pins = response.getArray();

            for (int i = 0; i < pins.length(); i++)
            {
                pinnedMessages.add(builder.createMessage(pins.getObject(i), MessageChannel.this, false));
            }

            return Collections.unmodifiableList(pinnedMessages);
        });
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageById(@Nonnull String messageId, @Nonnull CharSequence newContent)
    {
        Checks.isSnowflake(messageId, "Message ID");
        Checks.notEmpty(newContent, "Provided message content");
        Checks.check(newContent.length() <= 2000, "Provided newContent length must be 2000 or less characters.");

        Route.CompiledRoute route = Route.Messages.EDIT_MESSAGE.compile(getId(), messageId);
        if (newContent instanceof StringBuilder)
            return new MessageActionImpl(getJDA(), route, this, (StringBuilder) newContent);
        else
            return new MessageActionImpl(getJDA(), route, this).append(newContent);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageById(long messageId, @Nonnull CharSequence newContent)
    {
        return editMessageById(Long.toUnsignedString(messageId), newContent);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageById(@Nonnull String messageId, @Nonnull Message newContent)
    {
        Checks.isSnowflake(messageId, "Message ID");
        Checks.notNull(newContent, "message");

        Route.CompiledRoute route = Route.Messages.EDIT_MESSAGE.compile(getId(), messageId);
        return new MessageActionImpl(getJDA(), route, this).apply(newContent);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageById(long messageId, @Nonnull Message newContent)
    {
        return editMessageById(Long.toUnsignedString(messageId), newContent);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageFormatById(@Nonnull String messageId, @Nonnull String format, @Nonnull Object... args)
    {
        Checks.notBlank(format, "Format String");
        return editMessageById(messageId, String.format(format, args));
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageFormatById(long messageId, @Nonnull String format, @Nonnull Object... args)
    {
        Checks.notBlank(format, "Format String");
        return editMessageById(messageId, String.format(format, args));
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageById(@Nonnull String messageId, @Nonnull MessageEmbed newEmbed)
    {
        Checks.isSnowflake(messageId, "Message ID");
        Checks.notNull(newEmbed, "MessageEmbed");

        Route.CompiledRoute route = Route.Messages.EDIT_MESSAGE.compile(getId(), messageId);
        return new MessageActionImpl(getJDA(), route, this).embed(newEmbed);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction editMessageById(long messageId, @Nonnull MessageEmbed newEmbed)
    {
        return editMessageById(Long.toUnsignedString(messageId), newEmbed);
    }

    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        String out;

        out = upper ?  getName().toUpperCase(formatter.locale()) : getName();
        if (alt)
            out = "#" + out;

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
