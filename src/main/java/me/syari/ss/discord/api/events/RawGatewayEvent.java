

package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.sharding.DefaultShardManagerBuilder;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.utils.data.DataObject;

import javax.annotation.Nonnull;

/**
 * Wrapper for the raw dispatch event received from discord.
 * <br>This provides the raw structure of a gateway event through a {@link DataObject}
 * instance containing:
 * <ul>
 *     <li>d: The payload of the package (DataObject)</li>
 *     <li>t: The type of the package (String)</li>
 *     <li>op: The opcode of the package, always 0 for dispatch (int)</li>
 *     <li>s: The sequence number, equivalent to {@link #getResponseNumber()} (long)</li>
 * </ul>
 *
 * <p>Sent after derived events. This is disabled by default and can be enabled through either
 * the {@link JDABuilder#setRawEventsEnabled(boolean) JDABuilder}
 * or {@link DefaultShardManagerBuilder#setRawEventsEnabled(boolean) DefaultShardManagerBuilder}.
 *
 * @see JDABuilder#setRawEventsEnabled(boolean) JDABuilder.setRawEventsEnabled(boolean)
 * @see DefaultShardManagerBuilder#setRawEventsEnabled(boolean) DefaultShardManagerBuilder.setRawEventsEnabled(boolean)
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway" target="_blank">Gateway Documentation</a>
 */
public class RawGatewayEvent extends Event
{
    private final DataObject data;

    public RawGatewayEvent(@Nonnull JDA api, long responseNumber, @Nonnull DataObject data)
    {
        super(api, responseNumber);
        this.data = data;
    }

    /**
     * The raw gateway package including sequence and type.
     *
     * <ul>
     *     <li>d: The payload of the package (DataObject)</li>
     *     <li>t: The type of the package (String)</li>
     *     <li>op: The opcode of the package, always 0 for dispatch (int)</li>
     *     <li>s: The sequence number, equivalent to {@link #getResponseNumber()} (long)</li>
     * </ul>
     *
     * @return The data object
     */
    @Nonnull
    public DataObject getPackage()
    {
        return data;
    }

    /**
     * The payload of the package.
     *
     * @return The payload as a {@link DataObject} instance
     */
    @Nonnull
    public DataObject getPayload()
    {
        return data.getObject("d");
    }

    /**
     * The type of event.
     *
     * @return The type of event.
     */
    @Nonnull
    public String getType()
    {
        return data.getString("t");
    }
}
