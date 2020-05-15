

package net.dv8tion.jda.api.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.annotation.Nonnull;

/**
 * Wrapper for the raw dispatch event received from discord.
 * <br>This provides the raw structure of a gateway event through a {@link net.dv8tion.jda.api.utils.data.DataObject}
 * instance containing:
 * <ul>
 *     <li>d: The payload of the package (DataObject)</li>
 *     <li>t: The type of the package (String)</li>
 *     <li>op: The opcode of the package, always 0 for dispatch (int)</li>
 *     <li>s: The sequence number, equivalent to {@link #getResponseNumber()} (long)</li>
 * </ul>
 *
 * <p>Sent after derived events. This is disabled by default and can be enabled through either
 * the {@link net.dv8tion.jda.api.JDABuilder#setRawEventsEnabled(boolean) JDABuilder}
 * or {@link net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder#setRawEventsEnabled(boolean) DefaultShardManagerBuilder}.
 *
 * @see net.dv8tion.jda.api.JDABuilder#setRawEventsEnabled(boolean) JDABuilder.setRawEventsEnabled(boolean)
 * @see net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder#setRawEventsEnabled(boolean) DefaultShardManagerBuilder.setRawEventsEnabled(boolean)
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
     * @return The payload as a {@link net.dv8tion.jda.api.utils.data.DataObject} instance
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
