package me.syari.ss.discord.internal.utils;

import org.jetbrains.annotations.NotNull;

public class Checks {

    public static void check(final boolean expression, final String message) {
        if (!expression)
            throw new IllegalArgumentException(message);
    }

    public static void check(final boolean expression, final String message, final Object arg) {
        if (!expression)
            throw new IllegalArgumentException(String.format(message, arg));
    }

    public static void notEmpty(@NotNull final CharSequence argument, final String name) {
        if (Helpers.isEmpty(argument))
            throw new IllegalArgumentException(name + " may not be empty");
    }

}
