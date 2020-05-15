

package net.dv8tion.jda.api.utils.cache;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

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
     * Enables cache for {@link Member#getOnlineStatus(net.dv8tion.jda.api.entities.ClientType) Member.getOnlineStatus(ClientType)}
     */
    CLIENT_STATUS,
}
