

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.events.message.MessageBulkDeleteEvent;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.utils.data.DataObject;

import java.util.LinkedList;

public class MessageBulkDeleteHandler extends SocketHandler
{
    public MessageBulkDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        if (!content.isNull("guild_id"))
        {
            long guildId = content.getLong("guild_id");
            if (getJDA().getGuildSetupController().isLocked(guildId))
                return guildId;
        }
        final long channelId = content.getLong("channel_id");

        if (getJDA().isBulkDeleteSplittingEnabled())
        {
            SocketHandler handler = getJDA().getClient().getHandlers().get("MESSAGE_DELETE");
            content.getArray("ids").forEach(id ->
            {
                handler.handle(responseNumber, DataObject.empty()
                    .put("t", "MESSAGE_DELETE")
                    .put("d", DataObject.empty()
                        .put("channel_id", Long.toUnsignedString(channelId))
                        .put("id", id)));
            });
        }
        else
        {
            TextChannel channel = getJDA().getTextChannelById(channelId);
            if (channel == null)
            {
                getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, responseNumber, allContent, this::handle);
                EventCache.LOG.debug("Received a Bulk Message Delete for a TextChannel that is not yet cached.");
                return null;
            }

            if (getJDA().getGuildSetupController().isLocked(channel.getGuild().getIdLong()))
            {
                return channel.getGuild().getIdLong();
            }

            LinkedList<String> msgIds = new LinkedList<>();
            content.getArray("ids").forEach(id -> msgIds.add((String) id));
            getJDA().handleEvent(
                    new MessageBulkDeleteEvent(
                            getJDA(), responseNumber,
                            channel, msgIds));
        }
        return null;
    }
}
