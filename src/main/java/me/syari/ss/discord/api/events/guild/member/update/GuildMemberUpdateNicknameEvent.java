

package me.syari.ss.discord.api.events.guild.member.update;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link Member Member} updated their {@link Guild Guild} nickname.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve members who change their nickname, triggering guild, the old nick and the new nick.
 *
 * <p>Identifier: {@code nick}
 */
public class GuildMemberUpdateNicknameEvent extends GenericGuildMemberUpdateEvent<String>
{
    public static final String IDENTIFIER = "nick";

    public GuildMemberUpdateNicknameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable String oldNick)
    {
        super(api, responseNumber, member, oldNick, member.getNickname(), IDENTIFIER);
    }

    /**
     * The old nickname
     *
     * @return The old nickname
     */
    @Nullable
    public String getOldNickname()
    {
        return getOldValue();
    }

    /**
     * The new nickname
     *
     * @return The new nickname
     */
    @Nullable
    public String getNewNickname()
    {
        return getNewValue();
    }
}
