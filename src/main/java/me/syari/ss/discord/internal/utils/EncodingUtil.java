package me.syari.ss.discord.internal.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodingUtil {
    public static String encodeUTF8(String chars) {
        try {
            return URLEncoder.encode(chars, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e); // thanks JDK 1.4
        }
    }

    public static String encodeCodepointsUTF8(String input) {
        if (!input.startsWith("U+"))
            throw new IllegalArgumentException("Invalid format");
        String[] codePoints = input.substring(2).split("\\s*U\\+\\s*");
        StringBuilder encoded = new StringBuilder();
        for (String part : codePoints) {
            String utf16 = decodeCodepoint(part);
            String urlEncoded = encodeUTF8(utf16);
            encoded.append(urlEncoded);
        }
        return encoded.toString();
    }

    private static String decodeCodepoint(String hex) {
        int codePoint = Integer.parseUnsignedInt(hex, 16);
        return String.valueOf(Character.toChars(codePoint));
    }


    public static String encodeReaction(String unicode) {
        if (unicode.startsWith("U+") || unicode.startsWith("u+"))
            return encodeCodepointsUTF8(unicode);
        else
            return encodeUTF8(unicode);
    }
}
