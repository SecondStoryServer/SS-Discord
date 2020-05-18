package me.syari.ss.discord.internal.utils.config;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.utils.SessionController;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

public class SessionConfig {
    private final SessionController sessionController = new SessionController();
    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private final WebSocketFactory webSocketFactory = new WebSocketFactory();

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

}
