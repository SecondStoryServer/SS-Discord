package me.syari.ss.discord.api;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.hooks.ListenerAdapter;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.SessionControllerAdapter;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.managers.PresenceImpl;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;


public class JDABuilder {
    protected final String token;
    protected final ListenerAdapter listener;
    protected final ScheduledExecutorService rateLimitPool = null;
    protected final boolean shutdownRateLimitPool = true;
    protected final ScheduledExecutorService mainWsPool = null;
    protected final boolean shutdownMainWsPool = true;
    protected final ExecutorService callbackPool = null;
    protected final boolean shutdownCallbackPool = true;
    protected final EnumSet<CacheFlag> cacheFlags = EnumSet.allOf(CacheFlag.class);
    protected final ConcurrentMap<String, String> contextMap = null;
    protected SessionController controller = null;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected final OkHttpClient httpClient = null;
    protected final WebSocketFactory wsFactory = null;
    protected final IEventManager eventManager = null;
    protected final JDA.ShardInfo shardInfo = null;
    protected final Compression compression = Compression.ZLIB;
    protected final Activity activity = null;
    protected final OnlineStatus status = OnlineStatus.ONLINE;
    protected final boolean idle = false;
    protected final int maxReconnectDelay = 900;
    protected final int largeThreshold = 250;
    protected final int maxBufferSize = 2048;
    protected final EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected final ChunkingFilter chunkingFilter = ChunkingFilter.ALL;


    public JDABuilder(@Nullable String token, ListenerAdapter listener) {
        this.token = token;
        this.listener = listener;
    }


    @Nonnull
    public JDA build() throws LoginException {
        OkHttpClient httpClient = this.httpClient;
        if (httpClient == null) {
            if (this.httpClientBuilder == null)
                this.httpClientBuilder = new OkHttpClient.Builder();
            httpClient = this.httpClientBuilder.build();
        }

        WebSocketFactory wsFactory = this.wsFactory == null ? new WebSocketFactory() : this.wsFactory;

        if (controller == null && shardInfo != null)
            controller = new SessionControllerAdapter();

        AuthorizationConfig authConfig = new AuthorizationConfig(token);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        threadingConfig.setCallbackPool(callbackPool, shutdownCallbackPool);
        threadingConfig.setGatewayPool(mainWsPool, shutdownMainWsPool);
        threadingConfig.setRateLimitPool(rateLimitPool, shutdownRateLimitPool);
        SessionConfig sessionConfig = new SessionConfig(controller, httpClient, wsFactory, flags, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize, contextMap, cacheFlags, flags);

        JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig);
        jda.setChunkingFilter(chunkingFilter);

        if (eventManager != null)
            jda.setEventManager(eventManager);


        jda.setEventListener(listener);
        jda.setStatus(JDA.Status.INITIALIZED);

        ((PresenceImpl) jda.getPresence())
                .setCacheActivity(activity)
                .setCacheIdle(idle)
                .setCacheStatus(status);
        jda.login(shardInfo, compression, true);
        return jda;
    }

}
