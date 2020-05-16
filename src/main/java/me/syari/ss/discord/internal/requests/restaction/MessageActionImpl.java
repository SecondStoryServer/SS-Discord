package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.MessageType;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.requests.Method;
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
import java.util.*;
import java.util.function.BooleanSupplier;

public class MessageActionImpl extends RestActionImpl<Message> implements MessageAction {
    private static final String CONTENT_TOO_BIG = String.format("A message may not exceed %d characters. Please limit your input!", Message.MAX_CONTENT_LENGTH);
    protected final Map<String, InputStream> files = new HashMap<>();
    protected final Set<InputStream> ownedResources = new HashSet<>();
    protected final StringBuilder content;
    protected final MessageChannel channel;
    protected MessageEmbed embed = null;
    protected String nonce = null;
    protected boolean tts = false, override = false;

    public MessageActionImpl(JDA api, Route.CompiledRoute route, MessageChannel channel) {
        super(api, route);
        this.content = new StringBuilder();
        this.channel = channel;
    }

    public MessageActionImpl(JDA api, Route.CompiledRoute route, MessageChannel channel, StringBuilder contentBuilder) {
        super(api, route);
        Checks.check(contentBuilder.length() <= Message.MAX_CONTENT_LENGTH,
                "Cannot build a Message with more than %d characters. Please limit your input.", Message.MAX_CONTENT_LENGTH);
        this.content = contentBuilder;
        this.channel = channel;
    }

    @Nonnull
    @Override
    public MessageAction setCheck(BooleanSupplier checks) {
        return (MessageAction) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public MessageChannel getChannel() {
        return channel;
    }

    @Override
    public boolean isEmpty() {
        return Helpers.isBlank(content)
                && (embed == null || embed.isEmpty());
    }

    @Override
    public boolean isEdit() {
        return finalizeRoute().getMethod() == Method.PATCH;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl apply(final Message message) {
        if (message == null || message.getType() != MessageType.DEFAULT)
            return this;
        final List<MessageEmbed> embeds = message.getEmbeds();
        if (!embeds.isEmpty())
            embed(embeds.get(0));
        files.clear();

        return content(message.getContentRaw()).tts(message.isTTS());
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl tts(final boolean isTTS) {
        this.tts = isTTS;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl reset() {
        return content(null).nonce(null).embed(null).tts(false).override(false).clearFiles();
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl nonce(final String nonce) {
        this.nonce = nonce;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl content(final String content) {
        if (content == null || content.isEmpty())
            this.content.setLength(0);
        else if (content.length() <= Message.MAX_CONTENT_LENGTH)
            this.content.replace(0, this.content.length(), content);
        else
            throw new IllegalArgumentException(CONTENT_TOO_BIG);
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl embed(final MessageEmbed embed) {
        if (embed != null) {
            Checks.check(embed.isSendable(),
                    "Provided Message contains an empty embed or an embed with a length greater than %d characters, which is the max for Bot accounts!",
                    MessageEmbed.EMBED_MAX_LENGTH_BOT);
        }
        this.embed = embed;
        return this;
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

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl clearFiles() {
        files.clear();
        clearResources();
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public MessageActionImpl override(final boolean bool) {
        this.override = isEdit() && bool;
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
        // clear remaining resources, they will be closed after being sent
        files.clear();
        ownedResources.clear();
        return builder.build();
    }

    protected RequestBody asJSON() {
        return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toString());
    }

    protected DataObject getJSON() {
        final DataObject obj = DataObject.empty();
        if (override) {
            if (embed == null)
                obj.putNull("embed");
            else
                obj.put("embed", embed);
            if (content.length() == 0)
                obj.putNull("content");
            else
                obj.put("content", content.toString());
            if (nonce == null)
                obj.putNull("nonce");
            else
                obj.put("nonce", nonce);
            obj.put("tts", tts);
        } else {
            if (embed != null)
                obj.put("embed", embed);
            if (content.length() > 0)
                obj.put("content", content.toString());
            if (nonce != null)
                obj.put("nonce", nonce);
            obj.put("tts", tts);
        }
        return obj;
    }

    protected void checkFileAmount() {
        if (files.size() >= Message.MAX_FILE_AMOUNT)
            throw new IllegalStateException("Cannot add more than " + Message.MAX_FILE_AMOUNT + " files!");
    }

    protected void checkEdit() {
        if (isEdit())
            throw new IllegalStateException("Cannot add files to an existing message! Edit-Message does not support this operation!");
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
