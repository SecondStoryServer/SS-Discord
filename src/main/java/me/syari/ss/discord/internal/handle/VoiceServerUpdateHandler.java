

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.audio.AudioConnection;
import me.syari.ss.discord.api.hooks.VoiceDispatchInterceptor;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.managers.AudioManagerImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;

public class VoiceServerUpdateHandler extends SocketHandler
{
    public VoiceServerUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;
        Guild guild = getJDA().getGuildById(guildId);
        if (guild == null)
            throw new IllegalArgumentException("Attempted to start audio connection with Guild that doesn't exist!");

        getJDA().getDirectAudioController().update(guild, guild.getSelfMember().getVoiceState().getChannel());

        if (content.isNull("endpoint"))
        {
            //Discord did not provide an endpoint yet, we are to wait until discord has resources to provide
            // an endpoint, which will result in them sending another VOICE_SERVER_UPDATE which we will handle
            // to actually connect to the audio server.
            return null;
        }

        //Strip the port from the endpoint.
        String endpoint = content.getString("endpoint").replace(":80", "");
        String token = content.getString("token");
        String sessionId = guild.getSelfMember().getVoiceState().getSessionId();
        if (sessionId == null)
            throw new IllegalArgumentException("Attempted to create audio connection without having a session ID. Did VOICE_STATE_UPDATED fail?");

        VoiceDispatchInterceptor voiceInterceptor = getJDA().getVoiceInterceptor();
        if (voiceInterceptor != null)
        {
            voiceInterceptor.onVoiceServerUpdate(new VoiceDispatchInterceptor.VoiceServerUpdate(guild, endpoint, token, sessionId, allContent));
            return null;
        }

        AudioManagerImpl audioManager = (AudioManagerImpl) guild.getAudioManager();
        MiscUtil.locked(audioManager.CONNECTION_LOCK, () ->
        {
            //Synchronized to prevent attempts to close while setting up initial objects.
            if (audioManager.isConnected())
                audioManager.prepareForRegionChange();
            if (!audioManager.isAttemptingToConnect())
            {
                WebSocketClient.LOG.debug(
                    "Received a VOICE_SERVER_UPDATE but JDA is not currently connected nor attempted to connect " +
                    "to a VoiceChannel. Assuming that this is caused by another client running on this account. " +
                    "Ignoring the event.");
                return;
            }

            AudioConnection connection = new AudioConnection(audioManager, endpoint, sessionId, token);
            audioManager.setAudioConnection(connection);
            connection.startConnection();
        });
        return null;
    }
}
