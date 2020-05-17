package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

public interface Mentionable extends Formattable, ISnowflake {
    @NotNull
    String getAsMention();

    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        String out = upper ? getAsMention().toUpperCase(formatter.locale()) : getAsMention();

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
