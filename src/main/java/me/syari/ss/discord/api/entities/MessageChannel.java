package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.MessageActionImpl;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;


public interface MessageChannel extends ISnowflake, Formattable {

    @Nonnull
    String getName();


    @Nonnull
    ChannelType getType();


    @Nonnull
    JDA getJDA();


    @Nonnull
    @CheckReturnValue
    default MessageAction sendMessage(@Nonnull CharSequence text) {
        Checks.notEmpty(text, "Provided text for message");
        Checks.check(text.length() <= 2000, "Provided text for message must be less than 2000 characters in length");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        if (text instanceof StringBuilder)
            return new MessageActionImpl(getJDA(), route, this, (StringBuilder) text);
        else
            return new MessageActionImpl(getJDA(), route, this).append(text);
    }

    @Nonnull
    @CheckReturnValue
    default MessageAction sendMessage(@Nonnull Message msg) {
        Checks.notNull(msg, "Message");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        return new MessageActionImpl(getJDA(), route, this).apply(msg);
    }

    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        String out;

        out = upper ? getName().toUpperCase(formatter.locale()) : getName();
        if (alt)
            out = "#" + out;

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
