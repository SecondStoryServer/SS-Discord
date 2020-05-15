

package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.sharding.DefaultShardManagerBuilder;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.utils.data.DataObject;

import javax.annotation.Nonnull;


public class RawGatewayEvent extends Event
{
    private final DataObject data;

    public RawGatewayEvent(@Nonnull JDA api, long responseNumber, @Nonnull DataObject data)
    {
        super(api, responseNumber);
        this.data = data;
    }


    @Nonnull
    public DataObject getPackage()
    {
        return data;
    }


    @Nonnull
    public DataObject getPayload()
    {
        return data.getObject("d");
    }


    @Nonnull
    public String getType()
    {
        return data.getString("t");
    }
}
