package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.internal.entities.Message;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface MessageAction extends RestAction<Message>, Appendable {
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
}
