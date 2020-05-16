

package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class GatewayPingEvent extends Event implements UpdateEvent<JDA, Long>
{
    public static final String IDENTIFIER = "gateway-ping";
    private final long next, prev;

    public GatewayPingEvent(@Nonnull JDA api, long old)
    {
        super(api);
        this.next = api.getGatewayPing();
        this.prev = old;
    }


    public long getNewPing()
    {
        return next;
    }


    public long getOldPing()
    {
        return prev;
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier()
    {
        return IDENTIFIER;
    }

    @Nonnull
    @Override
    public JDA getEntity()
    {
        return getJDA();
    }

    @Nonnull
    @Override
    public Long getOldValue()
    {
        return prev;
    }

    @Nonnull
    @Override
    public Long getNewValue()
    {
        return next;
    }

    @Override
    public String toString()
    {
        return "GatewayUpdate[ping](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
