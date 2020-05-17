package me.syari.ss.discord.internal.utils.config;

import me.syari.ss.discord.internal.utils.Checks;

import org.jetbrains.annotations.NotNull;

public final class AuthorizationConfig {
    private String token;

    public AuthorizationConfig(@NotNull String token) {
        Checks.notNull(token, "Token");
        setToken(token);
    }

    @NotNull
    public String getToken() {
        return token;
    }

    public void setToken(@NotNull String token) {
        this.token = "Bot " + token;
    }
}
