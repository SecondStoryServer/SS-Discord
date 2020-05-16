package me.syari.ss.discord.internal.requests.restaction.order;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.restaction.order.OrderAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class OrderActionImpl<T, M extends OrderAction<T, M>>
        extends RestActionImpl<Void>
        implements OrderAction<T, M> {
    protected final List<T> orderList;
    protected final boolean ascendingOrder;
    protected int selectedPosition = -1;


    public OrderActionImpl(JDA api, Route.CompiledRoute route) {
        this(api, true, route);
    }


    public OrderActionImpl(JDA api, boolean ascendingOrder, Route.CompiledRoute route) {
        super(api, route);
        this.orderList = new ArrayList<>();
        this.ascendingOrder = ascendingOrder;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M setCheck(BooleanSupplier checks) {
        return (M) super.setCheck(checks);
    }

}
