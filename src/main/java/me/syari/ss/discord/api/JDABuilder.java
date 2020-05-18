package me.syari.ss.discord.api;

import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.function.Consumer;

public class JDABuilder {
    protected final String token;
    protected final Consumer<MessageReceivedEvent> messageReceivedEvent;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected final int maxReconnectDelay = 900;
    protected final int largeThreshold = 250;
    protected final int maxBufferSize = 2048;
    protected final ChunkingFilter chunkingFilter = ChunkingFilter.ALL;

    public JDABuilder(@NotNull String token, Consumer<MessageReceivedEvent> messageReceivedEvent) {
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Provided token was empty!");
        }
        this.token = token;
        this.messageReceivedEvent = messageReceivedEvent;
    }

    @NotNull
    public JDA build() throws LoginException {
        if (this.httpClientBuilder == null) {
            this.httpClientBuilder = new OkHttpClient.Builder();
        }

        OkHttpClient httpClient = this.httpClientBuilder.build();
        ThreadingConfig threadingConfig = new ThreadingConfig();
        SessionConfig sessionConfig = new SessionConfig(httpClient, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize);

        JDAImpl jda = new JDAImpl(token, sessionConfig, threadingConfig, metaConfig, chunkingFilter, messageReceivedEvent);

        jda.setStatus(JDA.Status.INITIALIZED);

        jda.login();
        return jda;
    }

}
