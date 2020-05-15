

package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;


public interface OrderAction<T, M extends OrderAction<T, M>> extends RestAction<Void>
{
    @Nonnull
    @Override
    M setCheck(@Nullable BooleanSupplier checks);

    
    boolean isAscendingOrder();

    
    @Nonnull
    List<T> getCurrentOrder();

    
    @Nonnull
    M selectPosition(int selectedPosition);

    
    @Nonnull
    M selectPosition(@Nonnull T selectedEntity);

    
    int getSelectedPosition();

    
    @Nonnull
    T getSelectedEntity();

    
    @Nonnull
    M moveUp(int amount);

    
    @Nonnull
    M moveDown(int amount);

    
    @Nonnull
    M moveTo(int position);

    
    @Nonnull
    M swapPosition(int swapPosition);

    
    @Nonnull
    M swapPosition(@Nonnull T swapEntity);

    
    @Nonnull
    M reverseOrder();

    
    @Nonnull
    M shuffleOrder();

    
    @Nonnull
    M sortOrder(@Nonnull final Comparator<T> comparator);
}
