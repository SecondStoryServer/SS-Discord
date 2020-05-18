package me.syari.ss.discord.internal.utils;

public class Checks {

    public static void check(final boolean expression, final String message) {
        if (!expression)
            throw new IllegalArgumentException(message);
    }

    public static void check(final boolean expression, final String message, final Object arg) {
        if (!expression)
            throw new IllegalArgumentException(String.format(message, arg));
    }

    public static void notNull(final Object argument, final String name) {
        if (argument == null)
            throw new IllegalArgumentException(name + " may not be null");
    }

    public static void notEmpty(final CharSequence argument, final String name) {
        notNull(argument, name);
        if (Helpers.isEmpty(argument))
            throw new IllegalArgumentException(name + " may not be empty");
    }

}
