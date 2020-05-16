package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;


public interface OrderAction<T, M extends OrderAction<T, M>> extends RestAction<Void> {
    @Nonnull
    @Override
    M setCheck(@Nullable BooleanSupplier checks);


}
