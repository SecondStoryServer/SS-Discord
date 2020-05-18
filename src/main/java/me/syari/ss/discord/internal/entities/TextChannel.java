package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class TextChannel implements ISnowflake, Comparable<TextChannel> {
    protected final long id;
    protected final SnowflakeReference<Guild> guild;
    protected final JDA api;
    protected String name;

    public TextChannel(long id, @NotNull Guild guild) {
        this.id = id;
        this.api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Guild getGuild() {
        return guild.resolve();
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @NotNull
    public String getAsMention() {
        return "<#" + id + '>';
    }

    @NotNull
    public JDA getJDA() {
        return api;
    }

    public void sendMessage(@NotNull String text) {
        Checks.notEmpty(text, "Provided text for message");
        Checks.check(text.length() <= 2000, "Provided text for message must be less than 2000 characters in length");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        MessageAction messageAction = new MessageAction(getJDA(), route, this, text);
        messageAction.queue();
    }

    @Override
    public String toString() {
        return "TextChannel:" + getName() + '(' + id + ')';
    }

    @Override
    public int compareTo(@NotNull TextChannel channel) {
        return Long.compareUnsigned(id, channel.getIdLong());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof TextChannel))
            return false;
        TextChannel channel = (TextChannel) obj;
        return channel.getIdLong() == getIdLong();
    }

    private static class MessageAction extends RestAction<Message> {
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

}
