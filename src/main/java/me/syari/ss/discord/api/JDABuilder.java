package me.syari.ss.discord.api;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.events.MessageReceivedEvent;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.function.Consumer;


public class JDABuilder {
    protected final String token;
    protected final Consumer<MessageReceivedEvent> messageReceivedEvent;
    protected final boolean shutdownRateLimitPool = true;
    protected final boolean shutdownMainWsPool = true;
    protected final boolean shutdownCallbackPool = true;
    protected final SessionController controller = null;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected final Compression compression = Compression.ZLIB;
    protected final int maxReconnectDelay = 900;
    protected final int largeThreshold = 250;
    protected final int maxBufferSize = 2048;
    protected final EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected final ChunkingFilter chunkingFilter = ChunkingFilter.ALL;


    public JDABuilder(@Nonnull String token, Consumer<MessageReceivedEvent> messageReceivedEvent) {
        this.token = token;
        this.messageReceivedEvent = messageReceivedEvent;
    }


    @Nonnull
    public JDA build() throws LoginException {
        if (this.httpClientBuilder == null)
            this.httpClientBuilder = new OkHttpClient.Builder();

        OkHttpClient httpClient = this.httpClientBuilder.build();

        WebSocketFactory wsFactory = new WebSocketFactory();

        AuthorizationConfig authConfig = new AuthorizationConfig(token);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        threadingConfig.setCallbackPool(null, shutdownCallbackPool);
        threadingConfig.setGatewayPool(null, shutdownMainWsPool);
        threadingConfig.setRateLimitPool(null, shutdownRateLimitPool);
        SessionConfig sessionConfig = new SessionConfig(null, httpClient, wsFactory, flags, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize, flags);

        JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig, messageReceivedEvent);
        jda.setChunkingFilter(chunkingFilter);

        jda.setStatus(JDA.Status.INITIALIZED);

        jda.login(null, compression, true);
        return jda;
    }

}
