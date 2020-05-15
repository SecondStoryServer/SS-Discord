

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.AttachmentOption;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;


public interface MessageAction extends RestAction<Message>, Appendable
{
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
    default MessageAction append(@Nonnull final CharSequence csq)
    {
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
    default MessageAction appendFormat(@Nonnull final String format, final Object... args)
    {
        return append(String.format(format, args));
    }


    @Nonnull
    @CheckReturnValue
    MessageAction addFile(@Nonnull final InputStream data, @Nonnull final String name, @Nonnull AttachmentOption... options);


    @Nonnull
    @CheckReturnValue
    default MessageAction addFile(@Nonnull final byte[] data, @Nonnull final String name, @Nonnull AttachmentOption... options)
    {
        Checks.notNull(data, "Data");
        final long maxSize = getJDA().getSelfUser().getAllowedFileSize();
        Checks.check(data.length <= maxSize, "File may not exceed the maximum file length of %d bytes!", maxSize);
        return addFile(new ByteArrayInputStream(data), name, options);
    }


    @Nonnull
    @CheckReturnValue
    default MessageAction addFile(@Nonnull final File file, @Nonnull AttachmentOption... options)
    {
        Checks.notNull(file, "File");
        return addFile(file, file.getName(), options);
    }


    @Nonnull
    @CheckReturnValue
    MessageAction addFile(@Nonnull final File file, @Nonnull final String name, @Nonnull AttachmentOption... options);


    @Nonnull
    @CheckReturnValue
    MessageAction clearFiles();


    @Nonnull
    @CheckReturnValue
    MessageAction clearFiles(@Nonnull BiConsumer<String, InputStream> finalizer);


    @Nonnull
    @CheckReturnValue
    MessageAction clearFiles(@Nonnull Consumer<InputStream> finalizer);


    @Nonnull
    @CheckReturnValue
    MessageAction override(final boolean bool);
}
