

package net.dv8tion.jda.api.events.self;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the avatar of the current user changed.
 *
 * <p>Can be used to retrieve the old avatar.
 *
 * <p>Identifier: {@code avatar}
 */
public class SelfUpdateAvatarEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "avatar";
    private static final String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s%s";

    public SelfUpdateAvatarEvent(@Nonnull JDA api, long responseNumber, @Nullable String oldAvatarId)
    {
        super(api, responseNumber, oldAvatarId, api.getSelfUser().getAvatarId(), IDENTIFIER);
    }

    /**
     * The old avatar id
     *
     * @return The old avatar id
     */
    @Nullable
    public String getOldAvatarId()
    {
        return getOldValue();
    }

    /**
     * The old avatar url
     *
     * @return  The old avatar url
     */
    @Nullable
    public String getOldAvatarUrl()
    {
        return previous == null ? null : String.format(AVATAR_URL, getSelfUser().getId(), previous, previous.startsWith("a_") ? ".gif" : ".png");
    }

    /**
     * The new avatar id
     *
     * @return The new avatar id
     */
    @Nullable
    public String getNewAvatarId()
    {
        return getNewValue();
    }

    /**
     * The new avatar url
     *
     * @return  The new avatar url
     */
    @Nullable
    public String getNewAvatarUrl()
    {
        return next == null ? null : String.format(AVATAR_URL, getSelfUser().getId(), next, next.startsWith("a_") ? ".gif" : ".png");
    }
}
