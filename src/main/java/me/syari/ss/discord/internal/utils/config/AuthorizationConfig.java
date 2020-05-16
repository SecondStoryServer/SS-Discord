package me.syari.ss.discord.internal.utils.config;

import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;

public final class AuthorizationConfig {
    private String token;

    public AuthorizationConfig(@Nonnull String token) {
        Checks.notNull(token, "Token");
        setToken(token);
    }

    @Nonnull
    public String getToken() {
        return token;
    }

    public void setToken(@Nonnull String token) {
        this.token = "Bot " + token;
    }
}
