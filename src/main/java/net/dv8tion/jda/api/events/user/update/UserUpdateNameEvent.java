

package net.dv8tion.jda.api.events.user.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that the username of a {@link net.dv8tion.jda.api.entities.User User} changed. (Not Nickname)
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve the User who changed their username and their previous username.
 *
 * <p>Identifier: {@code name}
 */
public class UserUpdateNameEvent extends GenericUserUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public UserUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull String oldName)
    {
        super(api, responseNumber, user, oldName, user.getName(), IDENTIFIER);
    }

    /**
     * The old username
     *
     * @return The old username
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new username
     *
     * @return The new username
     */
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
