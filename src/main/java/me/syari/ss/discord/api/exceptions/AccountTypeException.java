

package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.AccountType;

/**
 * Indicates that an operation is not possible unless the {@link AccountType AccountType}
 * matches the one provided in {@link #getRequiredType()}
 */
public class AccountTypeException extends RuntimeException
{
    private final AccountType requiredType;

    /**
     * Creates a new AccountTypeException instance
     *
     * @param requiredType
     *        The required {@link AccountType AccountType} for the operation
     */
    public AccountTypeException(AccountType requiredType)
    {
        this(requiredType, "The current AccountType is not valid for the attempted action. Required AccountType: " + requiredType);
    }

    /**
     * Creates a new AccountTypeException instance
     *
     * @param requiredType
     *        The required {@link AccountType AccountType} for the operation
     * @param message
     *        A specialized message
     */
    public AccountTypeException(AccountType requiredType, String message)
    {
        super(message);
        this.requiredType = requiredType;
    }

    /**
     * The required {@link AccountType AccountType} for the operation
     *
     * @return AccountType
     */
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
