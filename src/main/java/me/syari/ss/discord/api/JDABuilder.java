package me.syari.ss.discord.api;

import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.function.Consumer;

public class JDABuilder {
    protected final String token;
    protected final Consumer<MessageReceivedEvent> messageReceivedEvent;
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
        ThreadingConfig threadingConfig = new ThreadingConfig();
        SessionConfig sessionConfig = new SessionConfig(new OkHttpClient.Builder().build());

        JDA jda = new JDA(token, sessionConfig, threadingConfig, chunkingFilter, messageReceivedEvent);

        jda.setStatus(JDA.Status.INITIALIZED);

        jda.login();
        return jda;
    }

}
