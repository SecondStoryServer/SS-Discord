package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.entities.Mentionable;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.MessageAction;
import me.syari.ss.discord.internal.utils.Checks;

import org.jetbrains.annotations.NotNull;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

public class TextChannel extends AbstractChannelImpl<TextChannel> implements ISnowflake, Formattable, GuildChannel, Mentionable {

    public TextChannel(long id, Guild guild) {
        super(id, guild);
    }

    @Override
    public TextChannel setPosition(int rawPosition) {
        getGuild().getTextChannelsView().clearCachedLists();
        return super.setPosition(rawPosition);
    }

    @NotNull
    @Override
    public String getAsMention() {
        return "<#" + id + '>';
    }


    @NotNull
    public JDA getJDA() {
        return api;
    }

    @Override
    public String toString() {
        return "TC:" + getName() + '(' + id + ')';
    }

    @NotNull
    public MessageAction sendMessage(@NotNull CharSequence text) {
        Checks.notEmpty(text, "Provided text for message");
        Checks.check(text.length() <= 2000, "Provided text for message must be less than 2000 characters in length");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        if (text instanceof StringBuilder)
            return new MessageAction(getJDA(), route, this, (StringBuilder) text);
        else
            return new MessageAction(getJDA(), route, this).append(text);
    }


    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
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
