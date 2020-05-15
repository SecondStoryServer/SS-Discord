

package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessageReactionBulkRemoveHandler extends SocketHandler
{
    public MessageReactionBulkRemoveHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long messageId = content.getLong("message_id");
        final long channelId = content.getLong("channel_id");
        JDAImpl jda = getJDA();
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null)
        {
            jda.getEventCache().cache(EventCache.Type.CHANNEL, channelId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Received a reaction for a channel that JDA does not currently have cached channel_id: {} message_id: {}", channelId, messageId);
            return null;
        }

        switch (channel.getType())
        {
            case TEXT:
               jda.handleEvent(
                   new GuildMessageReactionRemoveAllEvent(
                       jda, responseNumber,
                       messageId, channel));
               break;
            case GROUP:
                WebSocketClient.LOG.error("Received a reaction bulk delete for a group which should not be possible");
                return null;
        }

        jda.handleEvent(
            new MessageReactionRemoveAllEvent(
                jda, responseNumber,
                messageId, channel));
        return null;
    }
}
