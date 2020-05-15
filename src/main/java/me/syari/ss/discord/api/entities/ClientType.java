

package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;

/**
 * The type of client a user might be active on.
 *
 * @see Member#getOnlineStatus(ClientType) Member.getOnlineStatus(type)
 */
public enum ClientType
{
    /** The official discord desktop client */
    DESKTOP("desktop"),
    /** The official discord mobile app */
    MOBILE("mobile"),
    /** Discord from the browser (or bot) */
    WEB("web"),
    /** Placeholder for a new type that is not yet supported here */
    UNKNOWN("unknown"),
    ;

    private final String key;

    ClientType(String key)
    {
        this.key = key;
    }

    /**
     * The raw key used by the API to identify this type
     *
     * @return The raw key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Resolves the provided raw API key to the enum constant.
     *
     * @param  key
     *         The api key to check
     *
     * @return The resolved ClientType or {@link #UNKNOWN}
     */
    @Nonnull
    public static ClientType fromKey(@Nonnull String key)
    {
        for (ClientType type : values())
        {
            if (type.key.equals(key))
                return type;
        }
        return UNKNOWN;
    }
}
