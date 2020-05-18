package me.syari.ss.discord.internal.utils;

public class Checks {
    public static void check(final boolean expression, final String message, final Object arg) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, arg));
        }
    }
}
