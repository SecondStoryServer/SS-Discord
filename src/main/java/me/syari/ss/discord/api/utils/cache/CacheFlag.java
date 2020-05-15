

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ClientType;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;

/**
 * Flags used to enable cache services for JDA
 */
public enum CacheFlag
{
    /**
     * Enables cache for {@link Member#getActivities()}
     */
    ACTIVITY,
    /**
     * Enables cache for {@link Member#getVoiceState()}
     * <br>This will always be cached for self member.
     */
    VOICE_STATE,
    /**
     * Enables cache for {@link Guild#getEmoteCache()}
     */
    EMOTE,
    /**
     * Enables cache for {@link Member#getOnlineStatus(ClientType) Member.getOnlineStatus(ClientType)}
     */
    CLIENT_STATUS,
}
