
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageType;
import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.api.events.message.guild.GuildMessageReceivedEvent;
import me.syari.ss.discord.api.events.message.priv.PrivateMessageReceivedEvent;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.entities.PrivateChannelImpl;
import me.syari.ss.discord.internal.entities.TextChannelImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;

public class MessageCreateHandler extends SocketHandler
{
    public MessageCreateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        MessageType type = MessageType.fromId(content.getInt("type"));

        if (type == MessageType.UNKNOWN)
        {
            WebSocketClient.LOG.debug("JDA received a message of unknown type. Type: {}  JSON: {}", type, content);
            return null;
        }

        JDAImpl jda = getJDA();
        if (!content.isNull("guild_id"))
        {
            long guildId = content.getLong("guild_id");
            if (jda.getGuildSetupController().isLocked(guildId))
                return guildId;
        }

        Message message;
        try
        {
            message = jda.getEntityBuilder().createMessage(content, true);
        }
        catch (IllegalArgumentException e)
        {
            switch (e.getMessage())
            {
                case EntityBuilder.MISSING_CHANNEL:
                {
                    final long channelId = content.getLong("channel_id");
                    jda.getEventCache().cache(EventCache.Type.CHANNEL, channelId, responseNumber, allContent, this::handle);
                    EventCache.LOG.debug("Received a message for a channel that JDA does not currently have cached");
                    return null;
                }
                case EntityBuilder.MISSING_USER:
                {
                    final long authorId = content.getObject("author").getLong("id");
                    jda.getEventCache().cache(EventCache.Type.USER, authorId, responseNumber, allContent, this::handle);
                    EventCache.LOG.debug("Received a message for a user that JDA does not currently have cached");
                    return null;
                }
                case EntityBuilder.UNKNOWN_MESSAGE_TYPE:
                {
                    WebSocketClient.LOG.debug("Ignoring message with unknown type: {}", content);
                    return null;
                }
                default:
                    throw e;
            }
        }

        switch (message.getChannelType())
        {
            case TEXT:
            {
                TextChannelImpl channel = (TextChannelImpl) message.getTextChannel();
                if (jda.getGuildSetupController().isLocked(channel.getGuild().getIdLong()))
                    return channel.getGuild().getIdLong();
                channel.setLastMessageId(message.getIdLong());
                jda.handleEvent(
                    new GuildMessageReceivedEvent(
                        jda, responseNumber,
                        message));
                break;
            }
            case PRIVATE:
            {
                PrivateChannelImpl channel = (PrivateChannelImpl) message.getPrivateChannel();
                channel.setLastMessageId(message.getIdLong());
                jda.handleEvent(
                    new PrivateMessageReceivedEvent(
                        jda, responseNumber,
                        message));
                break;
            }
            case GROUP:
                WebSocketClient.LOG.error("Received a MESSAGE_CREATE for a group channel which should not be possible");
                return null;
            default:
                WebSocketClient.LOG.warn("Received a MESSAGE_CREATE with a unknown MessageChannel ChannelType. JSON: {}", content);
                return null;
        }

        //Combo event
        jda.handleEvent(
            new MessageReceivedEvent(
                jda, responseNumber,
                message));
        return null;
    }
}
