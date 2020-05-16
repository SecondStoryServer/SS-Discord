package me.syari.ss.discord.internal.utils.config.sharding;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class ShardingSessionConfig extends SessionConfig {
    private final OkHttpClient.Builder builder;

    public ShardingSessionConfig(
            @Nullable SessionController sessionController,
            @Nullable OkHttpClient httpClient, @Nullable OkHttpClient.Builder httpClientBuilder,
            @Nullable WebSocketFactory webSocketFactory,
            EnumSet<ConfigFlag> flags,
            int maxReconnectDelay, int largeThreshold) {
        super(sessionController, httpClient, webSocketFactory, flags, maxReconnectDelay, largeThreshold);
        if (httpClient == null)
            this.builder = httpClientBuilder == null ? new OkHttpClient.Builder() : httpClientBuilder;
        else
            this.builder = null;
    }

    public SessionConfig toSessionConfig(OkHttpClient client) {
        return new SessionConfig(getSessionController(), client, getWebSocketFactory(), getFlags(), getMaxReconnectDelay(), getLargeThreshold());
    }

    @Nullable
    public OkHttpClient.Builder getHttpBuilder() {
        return builder;
    }

    @Nonnull
    public static ShardingSessionConfig getDefault() {
        return new ShardingSessionConfig(null, new OkHttpClient(), null, null, ConfigFlag.getDefault(), 900, 250);
    }
}
