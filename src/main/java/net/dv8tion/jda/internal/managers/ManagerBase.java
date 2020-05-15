

package net.dv8tion.jda.internal.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.managers.Manager;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class ManagerBase<M extends Manager<M>> extends AuditableRestActionImpl<Void> implements Manager<M>
{
    private static boolean enablePermissionChecks = true;
    protected long set = 0;

    public static void setPermissionChecksEnabled(boolean enable)
    {
        enablePermissionChecks = enable;
    }

    public static boolean isPermissionChecksEnabled()
    {
        return enablePermissionChecks;
    }

    protected ManagerBase(JDA api, Route.CompiledRoute route)
    {
        super(api, route);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M setCheck(BooleanSupplier checks)
    {
        return (M) super.setCheck(checks);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M reset(long fields)
    {
        //logic explanation:
        //0101 = fields
        //1010 = ~fields
        //1100 = set
        //1000 = set & ~fields
        set &= ~fields;
        return (M) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M reset(long... fields)
    {
        Checks.notNull(fields, "Fields");
        //trivial case
        if (fields.length == 0)
            return (M) this;
        else if (fields.length == 1)
            return reset(fields[0]);

        //complex case
        long sum = fields[0];
        for (int i = 1; i < fields.length; i++)
            sum |= fields[i];
        return reset(sum);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M reset()
    {
        set = 0;
        return (M) this;
    }

    @Override
    public void queue(Consumer<? super Void> success, Consumer<? super Throwable> failure)
    {
        if (shouldUpdate())
            super.queue(success, failure);
        else if (success != null)
            success.accept(null);
        else
            getDefaultSuccess().accept(null);
    }

    @Override
    public Void complete(boolean shouldQueue) throws RateLimitedException
    {
        if (shouldUpdate())
            return super.complete(shouldQueue);
        return null;
    }

    @Override
    protected BooleanSupplier finalizeChecks()
    {
        return enablePermissionChecks ? this::checkPermissions : super.finalizeChecks();
    }

    protected boolean shouldUpdate()
    {
        return set != 0;
    }

    protected boolean shouldUpdate(long bit)
    {
        return (set & bit) == bit;
    }

    protected <E> void withLock(E object, Consumer<? super E> consumer)
    {
        synchronized (object)
        {
            consumer.accept(object);
        }
    }

    protected boolean checkPermissions()
    {
        return true;
    }
}
