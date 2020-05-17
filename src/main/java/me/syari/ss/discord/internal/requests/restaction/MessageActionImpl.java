package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import me.syari.ss.discord.internal.utils.IOUtil;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageActionImpl extends RestActionImpl<Message> implements MessageAction {
    protected final Map<String, InputStream> files = new HashMap<>();
    protected final Set<InputStream> ownedResources = new HashSet<>();
    protected final StringBuilder content;
    protected final TextChannel channel;

    public MessageActionImpl(JDA api, Route.CompiledRoute route, TextChannel channel) {
        super(api, route);
        this.content = new StringBuilder();
        this.channel = channel;
    }

    public MessageActionImpl(JDA api, Route.CompiledRoute route, TextChannel channel, StringBuilder contentBuilder) {
        super(api, route);
        Checks.check(contentBuilder.length() <= Message.MAX_CONTENT_LENGTH,
                "Cannot build a Message with more than %d characters. Please limit your input.", Message.MAX_CONTENT_LENGTH);
        this.content = contentBuilder;
        this.channel = channel;
    }

    private boolean isEmpty() {
        return Helpers.isBlank(content);
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl append(final CharSequence csq, final int start, final int end) {
        if (content.length() + end - start > Message.MAX_CONTENT_LENGTH)
            throw new IllegalArgumentException("A message may not exceed 2000 characters. Please limit your input!");
        content.append(csq, start, end);
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl append(final char c) {
        if (content.length() == Message.MAX_CONTENT_LENGTH)
            throw new IllegalArgumentException("A message may not exceed 2000 characters. Please limit your input!");
        content.append(c);
        return this;
    }

    private void clearResources() {
        for (InputStream ownedResource : ownedResources) {
            try {
                ownedResource.close();
            } catch (IOException ex) {
                if (!ex.getMessage().toLowerCase().contains("closed"))
                    LOG.error("Encountered IOException trying to close owned resource", ex);
            }
        }
        ownedResources.clear();
    }

    protected RequestBody asMultipart() {
        final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        int index = 0;
        for (Map.Entry<String, InputStream> entry : files.entrySet()) {
            final RequestBody body = IOUtil.createRequestBody(Requester.MEDIA_TYPE_OCTET, entry.getValue());
            builder.addFormDataPart("file" + index++, entry.getKey(), body);
        }
        if (!isEmpty())
            builder.addFormDataPart("payload_json", getJSON().toString());
        files.clear();
        ownedResources.clear();
        return builder.build();
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
        if (!files.isEmpty())
            return asMultipart();
        else if (!isEmpty())
            return asJSON();
        throw new IllegalStateException("Cannot build a message without content!");
    }

    @Override
    protected void handleSuccess(Response response, Request<Message> request) {
        request.onSuccess(api.getEntityBuilder().createMessage(response.getObject(), channel, false));
    }

    @Override
    protected void finalize() {
        if (ownedResources.isEmpty())
            return;
        LOG.warn("Found unclosed resources in MessageAction instance, closing on finalization step!");
        clearResources();
    }
}
