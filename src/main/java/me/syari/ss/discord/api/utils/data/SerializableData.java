

package me.syari.ss.discord.api.utils.data;

import javax.annotation.Nonnull;

/**
 * Allows custom serialization for JSON payloads of an object.
 */
public interface SerializableData
{
    /**
     * Serialized {@link DataObject} for this object.
     *
     * @return {@link DataObject}
     */
    @Nonnull
    DataObject toData();
}
