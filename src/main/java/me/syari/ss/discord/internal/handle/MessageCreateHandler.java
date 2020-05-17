package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.requests.WebSocketClient;

import static me.syari.ss.discord.util.Check.isDefaultMessage;

public class MessageCreateHandler extends SocketHandler {
    public MessageCreateHandler(JDAImpl api) {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content) {
        System.out.println(">> MessageCreateHandler");

        if (!isDefaultMessage(content.getInt("type"))) {
            return null;
        }

        JDAImpl jda = getJDA();
        if (!content.isNull("guild_id")) {
            long guildId = content.getLong("guild_id");
            if (jda.getGuildSetupController().isLocked(guildId))
                return guildId;
        }

        Message message;
        try {
            message = jda.getEntityBuilder().createMessage(content, true);
        } catch (IllegalArgumentException ex) {
            switch (ex.getMessage()) {
                case EntityBuilder.MISSING_CHANNEL: {
                    final long channelId = content.getLong("channel_id");
                    jda.getEventCache().cache(EventCache.Type.CHANNEL, channelId, responseNumber, allContent, this::handle);
                    EventCache.LOG.debug("Received a message for a channel that JDA does not currently have cached");
                    return null;
                }
                case EntityBuilder.MISSING_USER: {
                    final long authorId = content.getObject("author").getLong("id");
                    jda.getEventCache().cache(EventCache.Type.USER, authorId, responseNumber, allContent, this::handle);
                    EventCache.LOG.debug("Received a message for a user that JDA does not currently have cached");
                    return null;
                }
                case EntityBuilder.UNKNOWN_MESSAGE_TYPE: {
                    WebSocketClient.LOG.debug("Ignoring message with unknown type: {}", content);
                    return null;
                }
                default:
                    throw ex;
            }
        }

        jda.callMessageReceiveEvent(message);
        return null;
    }
}
