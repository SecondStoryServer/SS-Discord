
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.events.user.UserTypingEvent;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.utils.data.DataObject;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TypingStartHandler extends SocketHandler
{

    public TypingStartHandler(JDAImpl api)
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
        MessageChannel channel = getJDA().getTextChannelsView().get(channelId);
        if (channel == null)
            channel = getJDA().getPrivateChannelsView().get(channelId);
        if (channel == null)
            channel = getJDA().getFakePrivateChannelMap().get(channelId);
        if (channel == null)
            return null;    //We don't have the channel cached yet. We chose not to cache this event
                            // because that happen very often and could easily fill up the EventCache if
                            // we, for some reason, never get the channel. Especially in an active channel.

//        if (channel instanceof TextChannel)
//        {
//            final long guildId = ((TextChannel) channel).getGuild().getIdLong();
//            if (getJDA().getGuildSetupController().isLocked(guildId))
//                return guildId;
//        }

        final long userId = content.getLong("user_id");
        User user;
        if (channel instanceof PrivateChannel)
            user = ((PrivateChannel) channel).getUser();
        else
            user = getJDA().getUsersView().get(userId);

        if (user == null)
            return null;    //Just like in the comment above, if for some reason we don't have the user
                            // then we will just throw the event away.

        OffsetDateTime timestamp = Instant.ofEpochSecond(content.getInt("timestamp")).atOffset(ZoneOffset.UTC);
        getJDA().handleEvent(
                new UserTypingEvent(
                        getJDA(), responseNumber,
                        user, channel, timestamp));
        return null;
    }
}
