

package net.dv8tion.jda.api.utils.data;

import javax.annotation.Nonnull;

/**
 * Allows custom serialization for JSON payloads of an object.
 */
public interface SerializableData
{
    /**
     * Serialized {@link net.dv8tion.jda.api.utils.data.DataObject} for this object.
     *
     * @return {@link net.dv8tion.jda.api.utils.data.DataObject}
     */
    @Nonnull
    DataObject toData();
}
