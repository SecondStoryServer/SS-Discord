package me.syari.ss.discord.internal.utils;

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

}
