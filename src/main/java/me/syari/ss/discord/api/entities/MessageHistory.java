

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.TimeUtil;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import org.apache.commons.collections4.map.ListOrderedMap;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UncheckedIOException;
import java.util.*;


public class MessageHistory
{
    protected final MessageChannel channel;

    protected final ListOrderedMap<Long, Message> history = new ListOrderedMap<>();


    public MessageHistory(@Nonnull MessageChannel channel)
    {
        Checks.notNull(channel, "Channel");
        this.channel = channel;
        if (channel instanceof TextChannel)
        {
            TextChannel tc = (TextChannel) channel;
            if (!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_HISTORY))
                throw new InsufficientPermissionException(tc, Permission.MESSAGE_HISTORY);
        }
    }


    @Nonnull
    public JDA getJDA()
    {
        return channel.getJDA();
    }


    public int size()
    {
        return history.size();
    }


    public boolean isEmpty()
    {
        return size() == 0;
    }


    @Nonnull
    public MessageChannel getChannel()
    {
        return channel;
    }


    @Nonnull
    @CheckReturnValue
    public RestAction<List<Message>> retrievePast(int amount)
    {
        if (amount > 100 || amount < 1)
            throw new IllegalArgumentException("Message retrieval limit is between 1 and 100 messages. No more, no less. Limit provided: " + amount);

        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()).withQueryParams("limit", Integer.toString(amount));

        if (!history.isEmpty())
            route = route.withQueryParams("before", String.valueOf(history.lastKey()));

        JDAImpl jda = (JDAImpl) getJDA();
        return new RestActionImpl<>(jda, route, (response, request) ->
        {
            EntityBuilder builder = jda.getEntityBuilder();
            LinkedList<Message> messages  = new LinkedList<>();
            DataArray historyJson = response.getArray();

            for (int i = 0; i < historyJson.length(); i++)
                messages.add(builder.createMessage(historyJson.getObject(i)));

            messages.forEach(msg -> history.put(msg.getIdLong(), msg));
            return messages;
        });
    }


    @Nonnull
    @CheckReturnValue
    public RestAction<List<Message>> retrieveFuture(int amount)
    {
        if (amount > 100 || amount < 1)
            throw new IllegalArgumentException("Message retrieval limit is between 1 and 100 messages. No more, no less. Limit provided: " + amount);

        if (history.isEmpty())
            throw new IllegalStateException("No messages have been retrieved yet, so there is no message to act as a marker to retrieve more recent messages based on.");

        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()).withQueryParams("limit", Integer.toString(amount), "after", String.valueOf(history.firstKey()));
        JDAImpl jda = (JDAImpl) getJDA();
        return new RestActionImpl<>(jda, route, (response, request) ->
        {
            EntityBuilder builder = jda.getEntityBuilder();
            LinkedList<Message> messages  = new LinkedList<>();
            DataArray historyJson = response.getArray();

            for (int i = 0; i < historyJson.length(); i++)
                messages.add(builder.createMessage(historyJson.getObject(i)));

            for (Iterator<Message> it = messages.descendingIterator(); it.hasNext();)
            {
                Message m = it.next();
                history.put(0, m.getIdLong(), m);
            }

            return messages;
        });
    }


    @Nonnull
    public List<Message> getRetrievedHistory()
    {
        int size = size();
        if (size == 0)
            return Collections.emptyList();
        else if (size == 1)
            return Collections.singletonList(history.getValue(0));
        return Collections.unmodifiableList(new ArrayList<>(history.values()));
    }


    @Nullable
    public Message getMessageById(@Nonnull String id)
    {
        return getMessageById(MiscUtil.parseSnowflake(id));
    }


    @Nullable
    public Message getMessageById(long id)
    {
        return history.get(id);
    }


    @Nonnull
    @CheckReturnValue
    public static MessageRetrieveAction getHistoryAfter(@Nonnull MessageChannel channel, @Nonnull String messageId)
    {
        checkArguments(channel, messageId);
        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()).withQueryParams("after", messageId);
        return new MessageRetrieveAction(route, channel);
    }


    @Nonnull
    @CheckReturnValue
    public static MessageRetrieveAction getHistoryBefore(@Nonnull MessageChannel channel, @Nonnull String messageId)
    {
        checkArguments(channel, messageId);
        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()).withQueryParams("before", messageId);
        return new MessageRetrieveAction(route, channel);
    }


    @Nonnull
    @CheckReturnValue
    public static MessageRetrieveAction getHistoryAround(@Nonnull MessageChannel channel, @Nonnull String messageId)
    {
        checkArguments(channel, messageId);
        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()).withQueryParams("around", messageId);
        return new MessageRetrieveAction(route, channel);
    }


    @Nonnull
    @CheckReturnValue
    public static MessageRetrieveAction getHistoryFromBeginning(@Nonnull MessageChannel channel)
    {
        return getHistoryAfter(channel, "0");
    }

    private static void checkArguments(MessageChannel channel, String messageId)
    {
        Checks.isSnowflake(messageId, "Message ID");
        Checks.notNull(channel, "Channel");
        if (channel.getType() == ChannelType.TEXT)
        {
            TextChannel t = (TextChannel) channel;
            if (!t.getGuild().getSelfMember().hasPermission(t, Permission.MESSAGE_HISTORY))
                throw new InsufficientPermissionException(t, Permission.MESSAGE_HISTORY);
        }
    }


    public static class MessageRetrieveAction extends RestActionImpl<MessageHistory>
    {
        private final MessageChannel channel;
        private Integer limit;

        protected MessageRetrieveAction(Route.CompiledRoute route, MessageChannel channel)
        {
            super(channel.getJDA(), route);
            this.channel = channel;
        }


        @Nonnull
        @CheckReturnValue
        public MessageRetrieveAction limit(@Nullable Integer limit)
        {
            if (limit != null)
            {
                Checks.positive(limit, "Limit");
                Checks.check(limit <= 100, "Limit may not exceed 100!");
            }
            this.limit = limit;
            return this;
        }

        @Override
        protected Route.CompiledRoute finalizeRoute()
        {
            final Route.CompiledRoute route = super.finalizeRoute();
            return limit == null ? route : route.withQueryParams("limit", String.valueOf(limit));
        }

        @Override
        protected void handleSuccess(Response response, Request<MessageHistory> request)
        {
            final MessageHistory result = new MessageHistory(channel);
            final DataArray array = response.getArray();
            final EntityBuilder builder = api.getEntityBuilder();
            for (int i = 0; i < array.length(); i++)
            {
                try
                {
                    DataObject obj = array.getObject(i);
                    result.history.put(obj.getLong("id"), builder.createMessage(obj, channel, false));
                }
                catch (UncheckedIOException | NullPointerException e)
                {
                    LOG.warn("Encountered exception in MessagePagination", e);
                }
            }
            request.onSuccess(result);
        }
    }
}
