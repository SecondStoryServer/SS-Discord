

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.Permission;

import javax.annotation.Nonnull;

/**
 * Represents an emote retrieved from {@link Guild#retrieveEmotes()} or {@link Guild#retrieveEmoteById(long)}
 *
 * @since 3.8.0
 *
 * @see   Emote Emote
 * @see   Guild#retrieveEmote(Emote)
 * @see   Guild#retrieveEmoteById(String)
 * @see   Guild#retrieveEmotes()
 */
public interface ListedEmote extends Emote
{
    /**
     * The user who created this Emote
     *
     * <p>This is only available for manually retrieved emotes from {@link Guild#retrieveEmotes()}
     * and {@link Guild#retrieveEmoteById(long)}.
     * <br>Requires {@link Permission#MANAGE_EMOTES Permission.MANAGE_EMOTES}.
     *
     * @throws IllegalStateException
     *         If this emote does not have user information
     *
     * @return The user who created this Emote
     *
     * @see    #hasUser()
     */
    @Nonnull
    User getUser();

    /**
     * Whether this Emote has information about the creator.
     * <br>If this is false, {@link #getUser()} throws an {@link IllegalStateException}.
     *
     * <p>This is only available for manually retrieved emotes from {@link Guild#retrieveEmotes()}
     * and {@link Guild#retrieveEmoteById(long)}.
     * <br>Requires {@link Permission#MANAGE_EMOTES Permission.MANAGE_EMOTES}.
     *
     * @return True, if this emote has an owner
     */
    boolean hasUser();
}
