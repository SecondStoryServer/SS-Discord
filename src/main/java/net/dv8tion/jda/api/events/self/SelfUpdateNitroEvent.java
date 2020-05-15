

package net.dv8tion.jda.api.events.self;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the current user subscribed to nitro or the active nitro subscription ran out. (client-only)
 *
 * <p>Can be used to track the state of the nitro subscription.
 *
 * <p>Identifier: {@code nitro}
 */
public class SelfUpdateNitroEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "nitro";

    public SelfUpdateNitroEvent(@Nonnull JDA api, long responseNumber, boolean wasNitro)
    {
        super(api, responseNumber, wasNitro, !wasNitro, IDENTIFIER);
    }

    /**
     * Whether or not a nitro subscription used to be active before.
     *
     * @return The old nitro subscription status.
     */
    public boolean wasNitro()
    {
        return getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getNewValue()
    {
        return super.getNewValue();
    }
}
