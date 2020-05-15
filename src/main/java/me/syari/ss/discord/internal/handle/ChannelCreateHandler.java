

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.events.channel.category.CategoryCreateEvent;
import me.syari.ss.discord.api.events.channel.priv.PrivateChannelCreateEvent;
import me.syari.ss.discord.api.events.channel.store.StoreChannelCreateEvent;
import me.syari.ss.discord.api.events.channel.text.TextChannelCreateEvent;
import me.syari.ss.discord.api.events.channel.voice.VoiceChannelCreateEvent;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.WebSocketClient;

public class ChannelCreateHandler extends SocketHandler
{
    public ChannelCreateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        ChannelType type = ChannelType.fromId(content.getInt("type"));

        long guildId = 0;
        JDAImpl jda = getJDA();
        if (type.isGuild())
        {
            guildId = content.getLong("guild_id");
            if (jda.getGuildSetupController().isLocked(guildId))
                return guildId;
        }

        EntityBuilder builder = jda.getEntityBuilder();
        switch (type)
        {
            case STORE:
            {
                builder.createStoreChannel(content, guildId);
                jda.handleEvent(
                    new StoreChannelCreateEvent(
                        jda, responseNumber,
                        builder.createStoreChannel(content, guildId)));
                break;
            }
            case TEXT:
            {
                jda.handleEvent(
                    new TextChannelCreateEvent(
                        jda, responseNumber,
                        builder.createTextChannel(content, guildId)));
                break;
            }
            case VOICE:
            {
                jda.handleEvent(
                    new VoiceChannelCreateEvent(
                        jda, responseNumber,
                        builder.createVoiceChannel(content, guildId)));
                break;
            }
            case CATEGORY:
            {
                jda.handleEvent(
                    new CategoryCreateEvent(
                        jda, responseNumber,
                        builder.createCategory(content, guildId)));
                break;
            }
            case PRIVATE:
            {
                jda.handleEvent(
                    new PrivateChannelCreateEvent(
                        jda, responseNumber,
                        builder.createPrivateChannel(content)));
                break;
            }
            case GROUP:
                WebSocketClient.LOG.warn("Received a CREATE_CHANNEL for a group which is not supported");
                return null;
            default:
                WebSocketClient.LOG.debug("Discord provided an CREATE_CHANNEL event with an unknown channel type! JSON: {}", content);
        }
        return null;
    }
}
