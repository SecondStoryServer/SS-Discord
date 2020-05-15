

package me.syari.ss.discord.internal.managers;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.managers.DirectAudioController;
import me.syari.ss.discord.internal.requests.WebSocketClient;

import javax.annotation.Nonnull;

public class DirectAudioControllerImpl implements DirectAudioController
{
    private final JDAImpl api;

    public DirectAudioControllerImpl(JDAImpl api)
    {
        this.api = api;
    }

    @Nonnull
    @Override
    public JDAImpl getJDA()
    {
        return api;
    }

    @Override
    public void connect(@Nonnull VoiceChannel channel)
    {
        Checks.notNull(channel, "Voice Channel");
        JDAImpl jda = getJDA();
        WebSocketClient client = jda.getClient();
        client.queueAudioConnect(channel);
    }

    @Override
    public void disconnect(@Nonnull Guild guild)
    {
        Checks.notNull(guild, "Guild");
        JDAImpl jda = getJDA();
        WebSocketClient client = jda.getClient();
        client.queueAudioDisconnect(guild);
    }

    @Override
    public void reconnect(@Nonnull VoiceChannel channel)
    {
        Checks.notNull(channel, "Voice Channel");
        JDAImpl jda = getJDA();
        WebSocketClient client = jda.getClient();
        client.queueAudioReconnect(channel);
    }


    public void update(Guild guild, VoiceChannel channel)
    {
        Checks.notNull(guild, "Guild");
        JDAImpl jda = getJDA();
        WebSocketClient client = jda.getClient();
        client.updateAudioConnection(guild.getIdLong(), channel);
    }
}
