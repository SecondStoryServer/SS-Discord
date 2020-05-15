

package me.syari.ss.discord.api;

import me.syari.ss.discord.annotations.Incubating;

/**
 * Represents the type of account that is logged in.
 * <br>Used to differentiate between Bots and Client accounts.
 */
public enum AccountType
{
    /** An OAuth2 Bot which was created by an application */
    BOT,
    /**
     * A User-Account which can be used via the official Discord Client
     *
     * @incubating This might not be usable in the future because these types are not to be used in automation
     */
    @Incubating CLIENT
}
