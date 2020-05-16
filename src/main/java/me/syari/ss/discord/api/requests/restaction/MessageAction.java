package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.AttachmentOption;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.function.BooleanSupplier;


public interface MessageAction extends RestAction<Message>, Appendable {
    @Nonnull
    @Override
    MessageAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    MessageChannel getChannel();


    boolean isEmpty();


    boolean isEdit();


    @Nonnull
    @CheckReturnValue
    MessageAction apply(@Nullable final Message message);


    @Nonnull
    @CheckReturnValue
    MessageAction tts(final boolean isTTS);


    @Nonnull
    @CheckReturnValue
    MessageAction reset();


    @Nonnull
    @CheckReturnValue
    MessageAction nonce(@Nullable final String nonce);


    @Nonnull
    @CheckReturnValue
    MessageAction content(@Nullable final String content);


    @Nonnull
    @CheckReturnValue
    MessageAction embed(@Nullable final MessageEmbed embed);


    @Nonnull
    @Override
    @CheckReturnValue
    default MessageAction append(@Nonnull final CharSequence csq) {
        return append(csq, 0, csq.length());
    }


    @Nonnull
    @Override
    @CheckReturnValue
    MessageAction append(@Nullable final CharSequence csq, final int start, final int end);


    @Nonnull
    @Override
    @CheckReturnValue
    MessageAction append(final char c);


    @Nonnull
    @CheckReturnValue
    MessageAction addFile(@Nonnull final InputStream data, @Nonnull final String name, @Nonnull AttachmentOption... options);


    @Nonnull
    @CheckReturnValue
    MessageAction clearFiles();


    @Nonnull
    @CheckReturnValue
    MessageAction override(final boolean bool);
}
