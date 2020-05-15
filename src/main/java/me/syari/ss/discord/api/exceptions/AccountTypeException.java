

package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.AccountType;


public class AccountTypeException extends RuntimeException
{
    private final AccountType requiredType;


    public AccountTypeException(AccountType requiredType)
    {
        this(requiredType, "The current AccountType is not valid for the attempted action. Required AccountType: " + requiredType);
    }


    public AccountTypeException(AccountType requiredType, String message)
    {
        super(message);
        this.requiredType = requiredType;
    }


    public AccountType getRequiredType()
    {
        return requiredType;
    }

    public static void check(AccountType actualType, AccountType requiredType)
    {
        if (actualType != requiredType)
            throw new AccountTypeException(requiredType);
    }
}
