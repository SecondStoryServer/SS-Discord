

package me.syari.ss.discord.internal.utils.config.sharding;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.audio.factory.IAudioSendFactory;
import me.syari.ss.discord.api.hooks.VoiceDispatchInterceptor;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import me.syari.ss.discord.internal.utils.config.flags.ShardingConfigFlag;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class ShardingSessionConfig extends SessionConfig
{
    private final OkHttpClient.Builder builder;
    private final IAudioSendFactory audioSendFactory;
    private final EnumSet<ShardingConfigFlag> shardingFlags;

    public ShardingSessionConfig(
            @Nullable SessionController sessionController, @Nullable VoiceDispatchInterceptor interceptor,
            @Nullable OkHttpClient httpClient, @Nullable OkHttpClient.Builder httpClientBuilder,
            @Nullable WebSocketFactory webSocketFactory, @Nullable IAudioSendFactory audioSendFactory,
            EnumSet<ConfigFlag> flags, EnumSet<ShardingConfigFlag> shardingFlags,
            int maxReconnectDelay, int largeThreshold)
    {
        super(sessionController, httpClient, webSocketFactory, interceptor, flags, maxReconnectDelay, largeThreshold);
        if (httpClient == null)
            this.builder = httpClientBuilder == null ? new OkHttpClient.Builder() : httpClientBuilder;
        else
            this.builder = null;
        this.audioSendFactory = audioSendFactory;
        this.shardingFlags = shardingFlags;
    }

    public SessionConfig toSessionConfig(OkHttpClient client)
    {
        return new SessionConfig(getSessionController(), client, getWebSocketFactory(), getVoiceDispatchInterceptor(), getFlags(), getMaxReconnectDelay(), getLargeThreshold());
    }

    public EnumSet<ShardingConfigFlag> getShardingFlags()
    {
        return this.shardingFlags;
    }

    @Nullable
    public OkHttpClient.Builder getHttpBuilder()
    {
        return builder;
    }

    @Nullable
    public IAudioSendFactory getAudioSendFactory()
    {
        return audioSendFactory;
    }

    @Nonnull
    public static ShardingSessionConfig getDefault()
    {
        return new ShardingSessionConfig(null, null, new OkHttpClient(), null, null, null, ConfigFlag.getDefault(), ShardingConfigFlag.getDefault(), 900, 250);
    }
}
