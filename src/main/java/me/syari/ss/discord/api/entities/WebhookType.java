

package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;

public enum WebhookType
{

    UNKNOWN(-1),

    INCOMING(1),

    FOLLOWER(2);

    private final int key;

    WebhookType(int key)
    {
        this.key = key;
    }

    /**
     * The raw api key for this type
     *
     * @return The api key, or -1 for {@link #UNKNOWN}
     */
    public int getKey()
    {
        return key;
    }

    /**
     * Resolves the provided raw api key to the corresponding webhook type.
     *
     * @param  key
     *         The key
     *
     * @return The WebhookType or {@link #UNKNOWN}
     */
    @Nonnull
    public static WebhookType fromKey(int key)
    {
        for (WebhookType type : values())
        {
            if (type.key == key)
                return type;
        }
        return UNKNOWN;
    }
}
