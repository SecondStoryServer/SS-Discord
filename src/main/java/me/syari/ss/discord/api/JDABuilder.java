package me.syari.ss.discord.api;

import me.syari.ss.discord.api.event.MessageReceivedEvent;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.function.Consumer;

public class JDABuilder {
    protected final String token;
    protected final Consumer<MessageReceivedEvent> messageReceivedEvent;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected final Compression compression = Compression.ZLIB;
    protected final int maxReconnectDelay = 900;
    protected final int largeThreshold = 250;
    protected final int maxBufferSize = 2048;
    protected final EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected final ChunkingFilter chunkingFilter = ChunkingFilter.ALL;

    public JDABuilder(@NotNull String token, Consumer<MessageReceivedEvent> messageReceivedEvent) {
        this.token = token;
        this.messageReceivedEvent = messageReceivedEvent;
    }

    @NotNull
    public JDA build() throws LoginException {
        if (this.httpClientBuilder == null) {
            this.httpClientBuilder = new OkHttpClient.Builder();
        }

        OkHttpClient httpClient = this.httpClientBuilder.build();
        AuthorizationConfig authConfig = new AuthorizationConfig(token);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        SessionConfig sessionConfig = new SessionConfig(httpClient, flags, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize, flags);

        JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig, messageReceivedEvent);
        jda.setChunkingFilter(chunkingFilter);

        jda.setStatus(JDA.Status.INITIALIZED);

        jda.login();
        return jda;
    }

}
