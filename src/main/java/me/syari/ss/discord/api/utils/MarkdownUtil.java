

package me.syari.ss.discord.api.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class MarkdownUtil
{
    private MarkdownUtil() {}

    
    @Nonnull
    public static String bold(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.BOLD);
        return "**" + sanitized + "**";
    }

    
    @Nonnull
    public static String italics(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.ITALICS_U);
        return "_" + sanitized + "_";
    }

    
    @Nonnull
    public static String underline(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.UNDERLINE);
        return "__" + sanitized + "__";
    }

    
    @Nonnull
    public static String monospace(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.MONO);
        return "`" + sanitized + "`";
    }

    
    @Nonnull
    public static String codeblock(@Nonnull String input)
    {
        return codeblock(null, input);
    }

    
    @Nonnull
    public static String codeblock(@Nullable String language, @Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.BLOCK);
        if (language != null)
            return "```" + language.trim() + "\n" + sanitized + "```";
        return "```" + sanitized + "```";
    }

    
    @Nonnull
    public static String spoiler(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.SPOILER);
        return "||" + sanitized + "||";
    }

    
    @Nonnull
    public static String strike(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.STRIKE);
        return "~~" + sanitized + "~~";
    }

    
    @Nonnull
    public static String quote(@Nonnull String input)
    {
        String sanitized = MarkdownSanitizer.escape(input, ~MarkdownSanitizer.QUOTE);
        return "> " + sanitized.replace("\n", "\n> ");
    }

    
    @Nonnull
    public static String quoteBlock(@Nonnull String input)
    {
        return ">>> " + input;
    }

    
    @Nonnull
    public static String maskedLink(@Nonnull String text, @Nonnull String url)
    {
        return "[" + text.replace("]", "\\]") + "](" + url.replace(")", "%29") + ")";
    }
}
