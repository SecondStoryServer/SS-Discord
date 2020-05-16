package me.syari.ss.discord.internal.requests.restaction.pagination;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.ParsingException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.pagination.MessagePaginationAction;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.Route;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MessagePaginationActionImpl
        extends PaginationActionImpl<Message, MessagePaginationAction>
        implements MessagePaginationAction {
    private final MessageChannel channel;

    public MessagePaginationActionImpl(MessageChannel channel) {
        super(channel.getJDA(), Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()), 1, 100, 100);

        if (channel.getType() == ChannelType.TEXT) {
            TextChannel textChannel = (TextChannel) channel;
            if (!textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_HISTORY))
                throw new InsufficientPermissionException(textChannel, Permission.MESSAGE_HISTORY);
        }

        this.channel = channel;
    }

    @Nonnull
    @Override
    public MessageChannel getChannel() {
        return channel;
    }

    @Override
    protected Route.CompiledRoute finalizeRoute() {
        Route.CompiledRoute route = super.finalizeRoute();

        final String limit = String.valueOf(this.getLimit());
        final long last = this.lastKey;

        route = route.withQueryParams("limit", limit);

        if (last != 0)
            route = route.withQueryParams("before", Long.toUnsignedString(last));

        return route;
    }

    @Override
    protected void handleSuccess(Response response, Request<List<Message>> request) {
        DataArray array = response.getArray();
        List<Message> messages = new ArrayList<>(array.length());
        EntityBuilder builder = api.getEntityBuilder();
        for (int i = 0; i < array.length(); i++) {
            try {
                Message msg = builder.createMessage(array.getObject(i), channel, false);
                messages.add(msg);
                if (useCache)
                    cached.add(msg);
                last = msg;
                lastKey = last.getIdLong();
            } catch (ParsingException | NullPointerException e) {
                LOG.warn("Encountered an exception in MessagePagination", e);
            } catch (IllegalArgumentException e) {
                if (EntityBuilder.UNKNOWN_MESSAGE_TYPE.equals(e.getMessage()))
                    LOG.warn("Skipping unknown message type during pagination", e);
                else
                    LOG.warn("Unexpected issue trying to parse message during pagination", e);
            }
        }

        request.onSuccess(messages);
    }

    @Override
    protected long getKey(Message it) {
        return it.getIdLong();
    }
}
