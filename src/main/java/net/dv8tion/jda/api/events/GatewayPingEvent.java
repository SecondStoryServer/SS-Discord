

package net.dv8tion.jda.api.events;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the gateway ping has been updated by the heartbeat cycle.
 * <br>You can always get the last ping update with {@link net.dv8tion.jda.api.JDA#getGatewayPing()}.
 *
 * <p>Can be used to detect changes to the gateway ping.
 *
 * <p>Identifier: {@code gateway-ping}
 */
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

    /**
     * The new ping for the current JDA session
     *
     * @return The new ping in milliseconds
     */
    public long getNewPing()
    {
        return next;
    }

    /**
     * The previous ping for the current JDA session
     *
     * @return The previous ping in milliseconds, or -1 if no ping was available yet
     */
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
