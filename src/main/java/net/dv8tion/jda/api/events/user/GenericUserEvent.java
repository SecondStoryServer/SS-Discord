
package net.dv8tion.jda.api.events.user;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.User User} changed or started an activity.
 * <br>Every UserEvent is derived from this event and can be casted.
 *
 * <p>Can be used to detect any UserEvent.
 */
public abstract class GenericUserEvent extends Event
{
    private final User user;

    public GenericUserEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user)
    {
        super(api, responseNumber);
        this.user = user;
    }

    /**
     * The related user instance
     *
     * @return The user instance related to this event
     */
    @Nonnull
    public User getUser()
    {
        return user;
    }
}
