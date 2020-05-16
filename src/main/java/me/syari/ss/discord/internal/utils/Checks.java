package me.syari.ss.discord.internal.utils;

import java.util.Collection;

public class Checks {

    public static void isSnowflake(final String snowflake, final String message) {
        notNull(snowflake, message);
        if (snowflake.length() > 20 || !Helpers.isNumeric(snowflake))
            throw new IllegalArgumentException(message + " is not a valid snowflake value!");
    }

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

    public static void notBlank(final CharSequence argument, final String name) {
        notNull(argument, name);
        if (Helpers.isBlank(argument))
            throw new IllegalArgumentException(name + " may not be blank");
    }

    public static void noWhitespace(final CharSequence argument, final String name) {
        notNull(argument, name);
        if (Helpers.containsWhitespace(argument))
            throw new IllegalArgumentException(name + " may not contain blanks");
    }

    public static void noneNull(final Collection<?> argument, final String name) {
        notNull(argument, name);
        argument.forEach(it -> notNull(it, name));
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

    public static void notNegative(final long n, final String name) {
        if (n < 0)
            throw new IllegalArgumentException(name + " may not be negative");
    }

}
