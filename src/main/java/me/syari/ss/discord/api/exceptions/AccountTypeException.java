package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.AccountType;


public class AccountTypeException extends RuntimeException {


    public AccountTypeException(AccountType requiredType) {
        this("The current AccountType is not valid for the attempted action. Required AccountType: " + requiredType);
    }


    public AccountTypeException(String message) {
        super(message);
    }


    public static void check(AccountType actualType, AccountType requiredType) {
        if (actualType != requiredType)
            throw new AccountTypeException(requiredType);
    }
}
