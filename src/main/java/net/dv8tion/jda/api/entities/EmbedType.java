

package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;

/**
 * Represents the embedded resource type.
 * <br>These are typically either Images, Videos or Links.
 */
public enum EmbedType
{
    IMAGE("image"),
    VIDEO("video"),
    LINK("link"),
    RICH("rich"),
    UNKNOWN("");

    private final String key;
    EmbedType(String key)
    {
        this.key = key;
    }

    /**
     * Attempts to find the EmbedType from the provided key.
     * <br>If the provided key doesn't match any known {@link net.dv8tion.jda.api.entities.EmbedType EmbedType},
     * this will return {@link net.dv8tion.jda.api.entities.EmbedType#UNKNOWN UNKNOWN}.
     *
     * @param  key
     *         The key related to the {@link net.dv8tion.jda.api.entities.EmbedType EmbedType}.
     *
     * @return The {@link net.dv8tion.jda.api.entities.EmbedType EmbedType} matching the provided key,
     *         or {@link net.dv8tion.jda.api.entities.EmbedType#UNKNOWN UNKNOWN}.
     */
    @Nonnull
    public static EmbedType fromKey(String key)
    {
        for (EmbedType type : values())
        {
            if (type.key.equals(key))
                return type;
        }
        return UNKNOWN;
    }
}
