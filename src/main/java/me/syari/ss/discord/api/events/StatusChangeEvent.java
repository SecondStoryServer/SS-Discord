
package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that our {@link JDA.Status Status} changed. (Example: SHUTTING_DOWN {@literal ->} SHUTDOWN)
 *
 * <br>Can be used to detect internal status changes. Possibly to log or forward on user's end.
 *
 * <p>Identifier: {@code status}
 */
public class StatusChangeEvent extends Event implements UpdateEvent<JDA, JDA.Status>
{
    public static final String IDENTIFIER = "status";

    protected final JDA.Status newStatus;
    protected final JDA.Status oldStatus;

    public StatusChangeEvent(@Nonnull JDA api, @Nonnull JDA.Status newStatus, @Nonnull JDA.Status oldStatus)
    {
        super(api);
        this.newStatus = newStatus;
        this.oldStatus = oldStatus;
    }

    /**
     * The status that we changed to
     *
     * @return The new status
     */
    @Nonnull
    public JDA.Status getNewStatus()
    {
        return newStatus;
    }

    /**
     * The previous status
     *
     * @return The previous status
     */
    @Nonnull
    public JDA.Status getOldStatus()
    {
        return oldStatus;
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
    public JDA.Status getOldValue()
    {
        return oldStatus;
    }

    @Nonnull
    @Override
    public JDA.Status getNewValue()
    {
        return newStatus;
    }

    @Override
    public String toString()
    {
        return "StatusUpdate(" + getOldStatus() + "->" + getNewStatus() + ')';
    }
}
