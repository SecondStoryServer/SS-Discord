

package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public class UnavailableGuildLeaveEvent extends Event
{
    private final long guildId;

    public UnavailableGuildLeaveEvent(@Nonnull JDA api, long responseNumber, long guildId)
    {
        super(api, responseNumber);
        this.guildId = guildId;
    }


    @Nonnull
    public String getGuildId()
    {
        return Long.toUnsignedString(guildId);
    }


    public long getGuildIdLong()
    {
        return guildId;
    }
}
