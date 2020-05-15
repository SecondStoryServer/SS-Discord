

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

    /**
     * Used to update the internal state of the voice request. When a connection
     * was successfully established JDA will stop sending requests for the initial connect.
     * <br>This is done to retry the voice updates in case of a partial service failure.
     *
     * <p>Should be called when:
     * <ol>
     *     <li>Receiving a Voice State Update for the current account and we were previously connected (moved or disconnected)</li>
     *     <li>Receiving a Voice Server Update (initial connect or region change)</li>
     * </ol>
     *
     * Note that the voice state update will always be received prior to a voice server update.
     * <br>The internal dispatch handlers already call this when needed, a library end-user never needs to call this method.
     *
     * @param guild
     *        The guild to update the state for
     * @param channel
     *        The new channel, or null to signal disconnect
     */
    public void update(Guild guild, VoiceChannel channel)
    {
        Checks.notNull(guild, "Guild");
        JDAImpl jda = getJDA();
        WebSocketClient client = jda.getClient();
        client.updateAudioConnection(guild.getIdLong(), channel);
    }
}
