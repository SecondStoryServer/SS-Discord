
package net.dv8tion.jda.api;

/**
 * Represents the online presence of a {@link net.dv8tion.jda.api.entities.Member Member}.
 */
public enum OnlineStatus
{
    /**
     * Indicates that the user is currently online (green circle)
     */
    ONLINE("online"),
    /**
     * Indicates that the user is currently idle (orange circle)
     */
    IDLE("idle"),
    /**
     * Indicates that the user is currently on do not disturb (red circle)
     * <br>This means the user won't receive notifications for mentions.
     */
    DO_NOT_DISTURB("dnd"),
    /**
     * Indicates that the currently logged in account is set to invisible and shows
     * up as {@link #OFFLINE} for other users.
     * <br>Only available for the currently logged in account.
     * <br>Other {@link net.dv8tion.jda.api.entities.Member Members} will show up as {@link net.dv8tion.jda.api.OnlineStatus#OFFLINE OFFLINE} even when they really are INVISIBLE.
     */
    INVISIBLE("invisible"),
    /**
     * Indicates that a member is currently offline or invisible (grey circle)
     */
    OFFLINE("offline"),
    /**
     * Placeholder for possible future online status values that are not listed here yet.
     */
    UNKNOWN("");

    private final String key;

    OnlineStatus(String key)
    {
        this.key = key;
    }

    /**
     * The valid API key for this OnlineStatus
     *
     * @return String representation of the valid API key for this OnlineStatus
     *
     * @see    <a href="https://discordapp.com/developers/docs/topics/gateway#presence-update">PRESENCE_UPDATE</a>
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Will get the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} from the provided key.
     * <br>If the provided key does not have a matching OnlineStatus, this will return {@link net.dv8tion.jda.api.OnlineStatus#UNKNOWN UNKONWN}
     *
     * @param  key
     *         The key relating to the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} we wish to retrieve.
     *
     * @return The matching {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus}. If there is no match, returns {@link net.dv8tion.jda.api.OnlineStatus#UNKNOWN UNKNOWN}.
     */
    public static OnlineStatus fromKey(String key)
    {
        for (OnlineStatus onlineStatus : values())
        {
            if (onlineStatus.key.equalsIgnoreCase(key))
            {
                return onlineStatus;
            }
        }
        return UNKNOWN;
    }
}
