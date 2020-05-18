package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class MessageAction extends RestAction<Message> implements Appendable {
    protected final Set<InputStream> ownedResources = new HashSet<>();
    protected final StringBuilder content;
    protected final TextChannel channel;

    public MessageAction(JDA api, Route.CompiledRoute route, TextChannel channel) {
        super(api, route);
        this.content = new StringBuilder();
        this.channel = channel;
    }

    public MessageAction(JDA api, Route.CompiledRoute route, TextChannel channel, @NotNull StringBuilder contentBuilder) {
        super(api, route);
        Checks.check(contentBuilder.length() <= Message.MAX_CONTENT_LENGTH,
                "Cannot build a Message with more than %d characters. Please limit your input.", Message.MAX_CONTENT_LENGTH);
        this.content = contentBuilder;
        this.channel = channel;
    }

    private boolean isNotEmpty() {
        return !Helpers.isBlank(content);
    }

    @NotNull
    @Override
    public MessageAction append(final CharSequence csq, final int start, final int end) {
        if (Message.MAX_CONTENT_LENGTH < content.length() + end - start) {
            throw new IllegalArgumentException("A message may not exceed 2000 characters. Please limit your input!");
        }
        content.append(csq, start, end);
        return this;
    }

    @NotNull
    @Override
    public MessageAction append(final char c) {
        if (content.length() == Message.MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("A message may not exceed 2000 characters. Please limit your input!");
        }
        content.append(c);
        return this;
    }

    @NotNull
    @Override
    public MessageAction append(@NotNull final CharSequence csq) {
        return append(csq, 0, csq.length());
    }

    private void clearResources() {
        for (InputStream ownedResource : ownedResources) {
            try {
                ownedResource.close();
            } catch (IOException ex) {
                if (!ex.getMessage().toLowerCase().contains("closed")) {
                    LOG.error("Encountered IOException trying to close owned resource", ex);
                }
            }
        }
        ownedResources.clear();
    }

    protected RequestBody asJSON() {
        return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toString());
    }

    protected DataObject getJSON() {
        final DataObject obj = DataObject.empty();
        obj.put("content", content.toString());
        return obj;
    }

    @Override
    protected RequestBody finalizeData() {
        if (isNotEmpty()) {
            return asJSON();
        }
        throw new IllegalStateException("Cannot build a message without content!");
    }

    @Override
    protected void handleSuccess(@NotNull Response response, @NotNull Request<Message> request) {
        request.onSuccess(api.getEntityBuilder().createMessage(response.getObject(), channel, false));
    }

    @Override
    protected void finalize() {
        if (ownedResources.isEmpty()) {
            return;
        }
        LOG.warn("Found unclosed resources in MessageAction instance, closing on finalization step!");
        clearResources();
    }
}
