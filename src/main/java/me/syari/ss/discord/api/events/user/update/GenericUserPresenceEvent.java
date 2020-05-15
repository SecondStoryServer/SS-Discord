

package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.GenericEvent;

import javax.annotation.Nonnull;


public interface GenericUserPresenceEvent extends GenericEvent
{

    @Nonnull
    Guild getGuild();


    @Nonnull
    Member getMember();
}
