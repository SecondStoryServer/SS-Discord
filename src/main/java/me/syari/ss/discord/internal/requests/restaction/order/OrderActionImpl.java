

package me.syari.ss.discord.internal.requests.restaction.order;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.restaction.order.OrderAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class OrderActionImpl<T, M extends OrderAction<T, M>>
    extends RestActionImpl<Void>
    implements OrderAction<T, M>
{
    protected final List<T> orderList;
    protected final boolean ascendingOrder;
    protected int selectedPosition = -1;


    public OrderActionImpl(JDA api, Route.CompiledRoute route)
    {
        this(api, true, route);
    }


    public OrderActionImpl(JDA api, boolean ascendingOrder, Route.CompiledRoute route)
    {
        super(api, route);
        this.orderList = new ArrayList<>();
        this.ascendingOrder = ascendingOrder;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M setCheck(BooleanSupplier checks)
    {
        return (M) super.setCheck(checks);
    }

    @Override
    public boolean isAscendingOrder()
    {
        return ascendingOrder;
    }

    @Nonnull
    @Override
    public List<T> getCurrentOrder()
    {
        return Collections.unmodifiableList(orderList);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M selectPosition(int selectedPosition)
    {
        Checks.notNegative(selectedPosition, "Provided selectedPosition");
        Checks.check(selectedPosition < orderList.size(), "Provided selectedPosition is too big and is out of bounds. selectedPosition: " + selectedPosition);

        this.selectedPosition = selectedPosition;

        return (M) this;
    }

    @Nonnull
    @Override
    public M selectPosition(@Nonnull T selectedEntity)
    {
        Checks.notNull(selectedEntity, "Channel");
        validateInput(selectedEntity);

        return selectPosition(orderList.indexOf(selectedEntity));
    }

    @Override
    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    @Nonnull
    @Override
    public T getSelectedEntity()
    {
        if (selectedPosition == -1)
            throw new IllegalStateException("No position has been selected yet");

        return orderList.get(selectedPosition);
    }

    @Nonnull
    @Override
    public M moveUp(int amount)
    {
        Checks.notNegative(amount, "Provided amount");
        if (selectedPosition == -1)
            throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
        if (ascendingOrder)
        {
            Checks.check(selectedPosition - amount >= 0,
                    "Amount provided to move up is too large and would be out of bounds." +
                            "Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList.size());
        }
        else
        {
            Checks.check(selectedPosition + amount < orderList.size(),
                    "Amount provided to move up is too large and would be out of bounds." +
                            "Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList.size());
        }

        if (ascendingOrder)
            return moveTo(selectedPosition - amount);
        else
            return moveTo(selectedPosition + amount);
    }

    @Nonnull
    @Override
    public M moveDown(int amount)
    {
        Checks.notNegative(amount, "Provided amount");
        if (selectedPosition == -1)
            throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");

        if (ascendingOrder)
        {
            Checks.check(selectedPosition + amount < orderList.size(),
                    "Amount provided to move down is too large and would be out of bounds." +
                            "Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList.size());
        }
        else
        {
            Checks.check(selectedPosition - amount >= orderList.size(),
                    "Amount provided to move down is too large and would be out of bounds." +
                            "Selected position: " + selectedPosition + " Amount: " + amount + " Largest Position: " + orderList.size());
        }

        if (ascendingOrder)
            return moveTo(selectedPosition + amount);
        else
            return moveTo(selectedPosition - amount);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M moveTo(int position)
    {
        Checks.notNegative(position, "Provided position");
        Checks.check(position < orderList.size(), "Provided position is too big and is out of bounds.");

        T selectedItem = orderList.remove(selectedPosition);
        orderList.add(position, selectedItem);

        return (M) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M swapPosition(int swapPosition)
    {
        Checks.notNegative(swapPosition, "Provided swapPosition");
        Checks.check(swapPosition < orderList.size(), "Provided swapPosition is too big and is out of bounds. swapPosition: "
                + swapPosition);

        T selectedItem = orderList.get(selectedPosition);
        T swapItem = orderList.get(swapPosition);
        orderList.set(swapPosition, selectedItem);
        orderList.set(selectedPosition, swapItem);

        return (M) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M swapPosition(@Nonnull T swapEntity)
    {
        Checks.notNull(swapEntity, "Provided swapEntity");
        validateInput(swapEntity);

        return swapPosition(orderList.indexOf(swapEntity));
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M reverseOrder()
    {
        Collections.reverse(this.orderList);
        return (M) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M shuffleOrder()
    {
        Collections.shuffle(this.orderList);
        return (M) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M sortOrder(@Nonnull final Comparator<T> comparator)
    {
        Checks.notNull(comparator, "Provided comparator");

        this.orderList.sort(comparator);
        return (M) this;
    }

    protected abstract void validateInput(T entity);
}
