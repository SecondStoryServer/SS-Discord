
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.message.MessageDeleteEvent;
import me.syari.ss.discord.api.events.message.guild.GuildMessageDeleteEvent;
import me.syari.ss.discord.api.events.message.priv.PrivateMessageDeleteEvent;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.PrivateChannelImpl;
import me.syari.ss.discord.internal.entities.TextChannelImpl;

public class MessageDeleteHandler extends SocketHandler
{

    public MessageDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long messageId = content.getLong("id");
        final long channelId = content.getLong("channel_id");

        MessageChannel channel = getJDA().getTextChannelById(channelId);
        if (channel == null)
        {
            channel = getJDA().getPrivateChannelById(channelId);
        }
        if (channel == null)
        {
            channel = getJDA().getFakePrivateChannelMap().get(channelId);
        }
        if (channel == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Got message delete for a channel/group that is not yet cached. ChannelId: {}", channelId);
            return null;
        }

        if (channel instanceof TextChannel)
        {
            TextChannelImpl tChan = (TextChannelImpl) channel;
            if (getJDA().getGuildSetupController().isLocked(tChan.getGuild().getIdLong()))
                return tChan.getGuild().getIdLong();
            if (tChan.hasLatestMessage() && messageId == channel.getLatestMessageIdLong())
                tChan.setLastMessageId(0); // Reset latest message id as it was deleted.
            getJDA().handleEvent(
                    new GuildMessageDeleteEvent(
                            getJDA(), responseNumber,
                            messageId, tChan));
        }
        else
        {
            PrivateChannelImpl pChan = (PrivateChannelImpl) channel;
            if (channel.hasLatestMessage() && messageId == channel.getLatestMessageIdLong())
                pChan.setLastMessageId(0); // Reset latest message id as it was deleted.
            getJDA().handleEvent(
                    new PrivateMessageDeleteEvent(
                            getJDA(), responseNumber,
                            messageId, pChan));
        }

        //Combo event
        getJDA().handleEvent(
                new MessageDeleteEvent(
                        getJDA(), responseNumber,
                        messageId, channel));
        return null;
    }
}
