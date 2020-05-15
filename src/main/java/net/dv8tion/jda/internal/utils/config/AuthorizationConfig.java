

package net.dv8tion.jda.internal.utils.config;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;

public final class AuthorizationConfig
{
    private final AccountType accountType;
    private String token;

    public AuthorizationConfig(@Nonnull AccountType accountType, @Nonnull String token)
    {
        Checks.notNull(accountType, "AccountType");
        Checks.notNull(token, "Token");
        this.accountType = accountType;
        setToken(token);
    }

    @Nonnull
    public AccountType getAccountType()
    {
        return accountType;
    }

    @Nonnull
    public String getToken()
    {
        return token;
    }

    public void setToken(@Nonnull String token)
    {
        if (getAccountType() == AccountType.BOT)
            this.token = "Bot " + token;
        else
            this.token = token;
    }
}
