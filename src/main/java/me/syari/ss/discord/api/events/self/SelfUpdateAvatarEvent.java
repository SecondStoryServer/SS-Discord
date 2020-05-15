

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class SelfUpdateAvatarEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "avatar";
    private static final String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s%s";

    public SelfUpdateAvatarEvent(@Nonnull JDA api, long responseNumber, @Nullable String oldAvatarId)
    {
        super(api, responseNumber, oldAvatarId, api.getSelfUser().getAvatarId(), IDENTIFIER);
    }


    @Nullable
    public String getOldAvatarId()
    {
        return getOldValue();
    }


    @Nullable
    public String getOldAvatarUrl()
    {
        return previous == null ? null : String.format(AVATAR_URL, getSelfUser().getId(), previous, previous.startsWith("a_") ? ".gif" : ".png");
    }


    @Nullable
    public String getNewAvatarId()
    {
        return getNewValue();
    }


    @Nullable
    public String getNewAvatarUrl()
    {
        return next == null ? null : String.format(AVATAR_URL, getSelfUser().getId(), next, next.startsWith("a_") ? ".gif" : ".png");
    }
}
