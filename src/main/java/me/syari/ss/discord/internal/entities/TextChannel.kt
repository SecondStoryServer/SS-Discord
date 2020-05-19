package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class TextChannel implements ISnowflake, Comparable<TextChannel> {
    private static final int MAX_CONTENT_LENGTH = 2000;

    protected final long id;
    protected final JDA api;
    protected final SnowflakeReference<Guild> guild;
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
        int length = text.length();
        if (length == 0) return;
        if (MAX_CONTENT_LENGTH < length) {
            sendMessage(text.substring(0, 2000));
            sendMessage(text.substring(2000));
            return;
        }
        Route route = Route.getSendMessageRoute(getId());
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
        if (obj == this) return true;
        if (!(obj instanceof TextChannel)) return false;
        TextChannel channel = (TextChannel) obj;
        return channel.getIdLong() == getIdLong();
    }

    private static class MessageAction extends RestAction<Message> {
        protected final Set<InputStream> ownedResources = new HashSet<>();
        protected final String content;
        protected final TextChannel channel;

        public MessageAction(JDA api, Route route, TextChannel channel, @NotNull String content) {
            super(api, route);
            this.content = content;
            this.channel = channel;
        }

        private void clearResources() {
            for (InputStream ownedResource : ownedResources) {
                try {
                    ownedResource.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            ownedResources.clear();
        }

        protected RequestBody asJSON() {
            final DataObject json = DataObject.empty();
            json.put("content", content);
            return RequestBody.create(Requester.MEDIA_TYPE_JSON, json.toString());
        }

        @Override
        protected RequestBody finalizeData() {
            return asJSON();
        }

        @Override
        protected void handleSuccess(@NotNull Response response, @NotNull Request<Message> request) {
            request.onSuccess(api.getEntityBuilder().createMessage(response.getDataObject(), channel, false));
        }

        @Override
        protected void finalize() {
            if (ownedResources.isEmpty()) return;
            clearResources();
        }
    }

}
