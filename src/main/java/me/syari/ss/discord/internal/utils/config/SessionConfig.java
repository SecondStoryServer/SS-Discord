package me.syari.ss.discord.internal.utils.config;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.utils.SessionController;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

public class SessionConfig {
    private final SessionController sessionController;
    private final OkHttpClient httpClient;
    private final WebSocketFactory webSocketFactory;
    private final int largeThreshold;
    private final int maxReconnectDelay;

    public SessionConfig(@NotNull OkHttpClient httpClient, int maxReconnectDelay, int largeThreshold) {
        this.sessionController = new SessionController();
        this.httpClient = httpClient;
        this.webSocketFactory = new WebSocketFactory();
        this.maxReconnectDelay = maxReconnectDelay;
        this.largeThreshold = largeThreshold;
    }

    @NotNull
    public SessionController getSessionController() {
        return sessionController;
    }

    @NotNull
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    @NotNull
    public WebSocketFactory getWebSocketFactory() {
        return webSocketFactory;
    }

    public int getMaxReconnectDelay() {
        return maxReconnectDelay;
    }

    public int getLargeThreshold() {
        return largeThreshold;
    }

}
