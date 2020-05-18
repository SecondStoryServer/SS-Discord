package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class MessageAction extends RestAction<Message> {
    protected final Set<InputStream> ownedResources = new HashSet<>();
    protected final String content;
    protected final TextChannel channel;

    public MessageAction(JDA api, Route.CompiledRoute route, TextChannel channel, @NotNull String content) {
        super(api, route);
        Checks.check(content.length() <= Message.MAX_CONTENT_LENGTH,
                "Cannot build a Message with more than %d characters. Please limit your input.", Message.MAX_CONTENT_LENGTH);
        this.content = content;
        this.channel = channel;
    }

    private boolean isNotEmpty() {
        return !Helpers.isBlank(content);
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
        obj.put("content", content);
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
