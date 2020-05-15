

package me.syari.ss.discord.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface UpdateEvent<E, T> extends GenericEvent
{

    @Nonnull
    @SuppressWarnings("unchecked")
    default Class<E> getEntityType()
    {
        return (Class<E>) getEntity().getClass();
    }


    @Nonnull
    String getPropertyIdentifier();


    @Nonnull
    E getEntity();


    @Nullable
    T getOldValue();


    @Nullable
    T getNewValue();
}
