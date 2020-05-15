

package net.dv8tion.jda.api.events;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

public interface GenericEvent
{
    /**
     * The current JDA instance corresponding to this Event
     *
     * @return The corresponding JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * The current sequence for this event.
     * <br>This can be used to keep events in order when making sequencing system.
     *
     * @return The current sequence number for this event
     */
    long getResponseNumber();
}
