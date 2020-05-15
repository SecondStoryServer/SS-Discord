

package me.syari.ss.discord.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a value of an entity was updated
 *
 * @param <E>
 *        The entity type
 * @param <T>
 *        The value type
 */
public interface UpdateEvent<E, T> extends GenericEvent
{
    /**
     * Class representation of the affected entity, useful when dealing with refection.
     *
     * @return The class of the affected entity
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default Class<E> getEntityType()
    {
        return (Class<E>) getEntity().getClass();
    }

    /**
     * The field name for the updated property
     *
     * <h1>Example</h1>
     * <pre><code>
     * {@literal @Override}
     * public void onGenericRoleUpdate(GenericRoleUpdateEvent event)
     * {
     *     switch (event.getPropertyIdentifier())
     *     {
     *     case RoleUpdateColorEvent.IDENTIFIER:
     *         System.out.printf("Updated color for role: %s%n", event);
     *         break;
     *     case RoleUpdatePositionEvent.IDENTIFIER:
     *         RoleUpdatePositionEvent update = (RoleUpdatePositionEvent) event;
     *         System.out.printf("Updated position for role: %s raw(%s{@literal ->}%s)%n", event, update.getOldPositionRaw(), update.getNewPositionRaw());
     *         break;
     *     default: return;
     *     }
     * }
     * </code></pre>
     *
     * @return The name of the updated property
     */
    @Nonnull
    String getPropertyIdentifier();

    /**
     * The affected entity
     *
     * @return The affected entity
     */
    @Nonnull
    E getEntity();

    /**
     * The old value
     *
     * @return The old value
     */
    @Nullable
    T getOldValue();

    /**
     * The new value
     *
     * @return The new value
     */
    @Nullable
    T getNewValue();
}
