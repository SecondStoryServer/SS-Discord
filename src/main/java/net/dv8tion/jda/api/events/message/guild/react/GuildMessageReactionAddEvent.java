

package net.dv8tion.jda.api.events.message.guild.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.MessageReaction MessageReaction} was added to a Message in a Guild
 *
 * <p>Can be used to detect when a reaction is added in a guild
 */
public class GuildMessageReactionAddEvent extends GenericGuildMessageReactionEvent
{
    public GuildMessageReactionAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull MessageReaction reaction)
    {
        super(api, responseNumber, member, reaction, member.getIdLong());
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public User getUser()
    {
        return super.getUser();
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Member getMember()
    {
        return super.getMember();
    }
}
