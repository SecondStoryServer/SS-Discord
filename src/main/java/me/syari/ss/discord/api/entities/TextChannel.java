package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.MiscUtil;

import java.util.FormattableFlags;
import java.util.Formatter;

public interface TextChannel extends GuildChannel, MessageChannel, IMentionable {
    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean uppercase = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean alternate = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        String out;

        if (alternate)
            out = "#" + (uppercase ? getName().toUpperCase(formatter.locale()) : getName());
        else
            out = getAsMention();

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
