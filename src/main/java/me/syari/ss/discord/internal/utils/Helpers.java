package me.syari.ss.discord.internal.utils;

import org.jetbrains.annotations.NotNull;

public final class Helpers {
    public static boolean isEmpty(final CharSequence seq) {
        return seq == null || seq.length() == 0;
    }

    public static boolean isBlank(final CharSequence seq) {
        if (isEmpty(seq))
            return true;
        for (int i = 0; i < seq.length(); i++) {
            if (!Character.isWhitespace(seq.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int countMatches(final CharSequence seq, final char c) {
        if (isEmpty(seq)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < seq.length(); i++) {
            if (seq.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static String truncate(final String input, final int maxWidth) {
        if (input == null) {
            return null;
        }
        Checks.notNegative(maxWidth, "maxWidth");
        if (input.length() <= maxWidth) {
            return input;
        }
        if (maxWidth == 0) {
            return "";
        }
        return input.substring(0, maxWidth);
    }

    public static @NotNull String rightPad(final @NotNull String input, final int size) {
        int pads = size - input.length();
        if (pads <= 0)
            return input;
        StringBuilder out = new StringBuilder(input);
        for (int i = pads; i > 0; i--)
            out.append(' ');
        return out.toString();
    }

    public static @NotNull String leftPad(final @NotNull String input, final int size) {
        int pads = size - input.length();
        if (pads <= 0)
            return input;
        StringBuilder out = new StringBuilder();
        for (int i = pads; i > 0; i--)
            out.append(' ');
        return out.append(input).toString();
    }
}
