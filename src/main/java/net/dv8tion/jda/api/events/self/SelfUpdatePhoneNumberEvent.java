

package net.dv8tion.jda.api.events.self;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the phone number associated with your account changed. (client-only)
 *
 * <p>Can be used to retrieve the old phone number.
 *
 * <p>Identifier: {@code phone}
 */
public class SelfUpdatePhoneNumberEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "phone";

    public SelfUpdatePhoneNumberEvent(@Nonnull JDA api, long responseNumber, @Nullable String oldPhoneNumber)
    {
        super(api, responseNumber, oldPhoneNumber, api.getSelfUser().getPhoneNumber(), IDENTIFIER);
    }

    /**
     * The old phone number or {@code null} if no phone number was previously set.
     *
     * @return The old phone number or {@code null}.
     */
    @Nullable
    public String getOldPhoneNumber()
    {
        return getOldValue();
    }

    /**
     * The new phone number.
     *
     * @return The new phone number
     */
    @Nullable
    public String getNewPhoneNumber()
    {
        return getNewValue();
    }
}
