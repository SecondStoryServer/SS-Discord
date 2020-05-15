

package net.dv8tion.jda.api.entities;

import net.dv8tion.jda.api.utils.TimeUtil;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;

/**
 * Marks a snowflake entity. Snowflake entities are ones that have an id that uniquely identifies them.
 *
 * @since 3.0
 */
public interface ISnowflake
{
    /**
     * The Snowflake id of this entity. This is unique to every entity and will never change.
     *
     * @return Never-null String containing the Id.
     */
    @Nonnull
    default String getId()
    {
        return Long.toUnsignedString(getIdLong());
    }

    /**
     * The Snowflake id of this entity. This is unique to every entity and will never change.
     *
     * @return Long containing the Id.
     */
    long getIdLong();

    /**
     * The time this entity was created. Calculated through the Snowflake in {@link #getIdLong}.
     *
     * @return OffsetDateTime - Time this entity was created at.
     *
     * @see    TimeUtil#getTimeCreated(long)
     */
    @Nonnull
    default OffsetDateTime getTimeCreated()
    {
        return TimeUtil.getTimeCreated(getIdLong());
    }
}
