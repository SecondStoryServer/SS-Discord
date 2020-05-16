
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.WebhookAction;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.List;


public interface TextChannel extends GuildChannel, MessageChannel, IMentionable
{

    int MAX_SLOWMODE = 21600;


    @Nullable
    String getTopic();


    boolean isNSFW();


    int getSlowmode();


    @Nonnull
    @CheckReturnValue
    RestAction<List<Webhook>> retrieveWebhooks();


    @Nonnull
    @CheckReturnValue
    WebhookAction createWebhook(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> deleteMessages(@Nonnull Collection<Message> messages);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> deleteMessagesByIds(@Nonnull Collection<String> messageIds);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> deleteWebhookById(@Nonnull String id);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> clearReactionsById(@Nonnull String messageId);


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> clearReactionsById(long messageId)
    {
        return clearReactionsById(Long.toUnsignedString(messageId));
    }


    @Nonnull
    @CheckReturnValue
    RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull String unicode, @Nonnull User user);


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(long messageId, @Nonnull String unicode, @Nonnull User user)
    {
        return removeReactionById(Long.toUnsignedString(messageId), unicode, user);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull Emote emote, @Nonnull User user)
    {
        Checks.notNull(emote, "Emote");
        return removeReactionById(messageId, emote.getName() + ":" + emote.getId(), user);
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<Void> removeReactionById(long messageId, @Nonnull Emote emote, @Nonnull User user)
    {
        return removeReactionById(Long.toUnsignedString(messageId), emote, user);
    }


    boolean canTalk();


    boolean canTalk(@Nonnull Member member);

    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        String out;

        if (alt)
            out = "#" + (upper ?  getName().toUpperCase(formatter.locale()) : getName());
        else
            out = getAsMention();

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
