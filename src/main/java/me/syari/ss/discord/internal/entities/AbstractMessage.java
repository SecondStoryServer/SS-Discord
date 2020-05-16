

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.internal.utils.Helpers;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.List;

public abstract class AbstractMessage implements Message
{

    protected final String content;
    protected final boolean isTTS;

    public AbstractMessage(String content, boolean isTTS)
    {
        this.content = content;
        this.isTTS = isTTS;
    }

    @Nonnull
    @Override
    public String getContentRaw()
    {
        return content;
    }

    @Override
    public boolean isTTS()
    {
        return isTTS;
    }

    protected abstract void unsupported();

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;

        String out = content;

        if (upper)
            out = out.toUpperCase(formatter.locale());

        appendFormat(formatter, width, precision, leftJustified, out);
    }

    protected void appendFormat(Formatter formatter, int width, int precision, boolean leftJustified, String out)
    {
        try
        {
            Appendable appendable = formatter.out();
            if (precision > -1 && out.length() > precision)
            {
                appendable.append(Helpers.truncate(out, precision - 3)).append("...");
                return;
            }

            if (leftJustified)
                appendable.append(Helpers.rightPad(out, width));
            else
                appendable.append(Helpers.leftPad(out, width));
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Nonnull
    @Override
    public List<User> getMentionedUsers()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public List<TextChannel> getMentionedChannels()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public List<Role> getMentionedRoles()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public List<IMentionable> getMentions(@Nonnull MentionType... types)
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull MentionType... types)
    {
        unsupported();
        return false;
    }

    @Nonnull
    @Override
    public User getAuthor()
    {
        unsupported();
        return null;
    }

    @Override
    public Member getMember()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public String getContentDisplay()
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isFromType(@Nonnull ChannelType type)
    {
        unsupported();
        return false;
    }

    @Nonnull
    @Override
    public ChannelType getChannelType()
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isWebhookMessage()
    {
        unsupported();
        return false;
    }

    @Nonnull
    @Override
    public MessageChannel getChannel()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public PrivateChannel getPrivateChannel()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public TextChannel getTextChannel()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public Guild getGuild()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public List<MessageEmbed> getEmbeds()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public List<Emote> getEmotes()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public MessageType getType()
    {
        unsupported();
        return null;
    }
}
