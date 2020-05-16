package me.syari.ss.discord.internal.utils;

public class Checks {

    public static void check(final boolean expression, final String message) {
        if (!expression)
            throw new IllegalArgumentException(message);
    }

    public static void check(final boolean expression, final String message, final Object... args) {
        if (!expression)
            throw new IllegalArgumentException(String.format(message, args));
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

    public static void noWhitespace(final CharSequence argument, final String name) {
        notNull(argument, name);
        if (Helpers.containsWhitespace(argument))
            throw new IllegalArgumentException(name + " may not contain blanks");
    }

    public static void noneNull(final Object[] argument, final String name) {
        notNull(argument, name);
        for (Object it : argument) {
            notNull(it, name);
        }
    }

    public static void notNegative(final int n, final String name) {
        if (n < 0)
            throw new IllegalArgumentException(name + " may not be negative");
    }

}
