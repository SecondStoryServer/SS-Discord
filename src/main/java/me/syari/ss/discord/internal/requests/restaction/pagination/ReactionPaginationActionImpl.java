

package me.syari.ss.discord.internal.requests.restaction.pagination;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.exceptions.ParsingException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.pagination.ReactionPaginationAction;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.EncodingUtil;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.utils.data.DataArray;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ReactionPaginationActionImpl
    extends PaginationActionImpl<User, ReactionPaginationAction>
    implements ReactionPaginationAction
{
    protected final MessageReaction reaction;

    /**
     * Creates a new PaginationAction instance
     *
     * @param reaction
     *        The target {@link MessageReaction MessageReaction}
     */
    public ReactionPaginationActionImpl(MessageReaction reaction)
    {
        super(reaction.getJDA(), Route.Messages.GET_REACTION_USERS.compile(reaction.getChannel().getId(), reaction.getMessageId(), getCode(reaction)), 1, 100, 100);
        this.reaction = reaction;
    }

    public ReactionPaginationActionImpl(Message message, String code)
    {
        super(message.getJDA(), Route.Messages.GET_REACTION_USERS.compile(message.getChannel().getId(), message.getId(), code), 1, 100, 100);
        this.reaction = null;
    }

    protected static String getCode(MessageReaction reaction)
    {
        MessageReaction.ReactionEmote emote = reaction.getReactionEmote();

        return emote.isEmote()
            ? emote.getName() + ":" + emote.getId()
            : EncodingUtil.encodeUTF8(emote.getName());
    }

    @Nonnull
    @Override
    public MessageReaction getReaction()
    {
        if (reaction == null)
            throw new IllegalStateException("Cannot get reaction for this action");
        return reaction;
    }

    @Override
    protected Route.CompiledRoute finalizeRoute()
    {
        Route.CompiledRoute route = super.finalizeRoute();

        String after = null;
        String limit = String.valueOf(getLimit());
        long last = this.lastKey;
        if (last != 0)
            after = Long.toUnsignedString(last);

        route = route.withQueryParams("limit", limit);

        if (after != null)
            route = route.withQueryParams("after", after);

        return route;
    }

    @Override
    protected void handleSuccess(Response response, Request<List<User>> request)
    {
        final EntityBuilder builder = api.getEntityBuilder();
        final DataArray array = response.getArray();
        final List<User> users = new LinkedList<>();
        for (int i = 0; i < array.length(); i++)
        {
            try
            {
                final User user = builder.createFakeUser(array.getObject(i), false);
                users.add(user);
                if (useCache)
                    cached.add(user);
                last = user;
                lastKey = last.getIdLong();
            }
            catch (ParsingException | NullPointerException e)
            {
                RestActionImpl.LOG.warn("Encountered exception in ReactionPagination", e);
            }
        }

        request.onSuccess(users);
    }

    @Override
    protected long getKey(User it)
    {
        return it.getIdLong();
    }
}
