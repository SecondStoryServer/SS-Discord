package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nullable;
import java.util.FormattableFlags;
import java.util.Formatter;

public interface TextChannel extends GuildChannel, MessageChannel, IMentionable {

    int MAX_SLOWMODE = 21600;


    @Nullable
    String getTopic();


    boolean isNSFW();


    int getSlowmode();


    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        String out;

        if (alt)
            out = "#" + (upper ? getName().toUpperCase(formatter.locale()) : getName());
        else
            out = getAsMention();

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
