

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * {@link CacheView CacheView} implementation
 * specifically to combine {@link Member Member} cache views.
 *
 * <p>This is done because Members do not implement {@link ISnowflake ISnowflake} as
 * they are not globally unique but only unique per {@link Guild Guild}!
 *
 * @see CacheView CacheView for details on Efficient Memory Usage
 */
public interface UnifiedMemberCacheView extends CacheView<Member>
{
    /**
     * Retrieves all member represented by the provided ID.
     *
     * @param  id
     *         The ID of the members
     *
     * @return Possibly-empty unmodifiable list of member for the specified ID
     */
    @Nonnull
    List<Member> getElementsById(long id);

    /**
     * Retrieves all member represented by the provided ID.
     *
     * @param  id
     *         The ID of the members
     *
     * @throws java.lang.NumberFormatException
     *         If the provided String is {@code null} or
     *         cannot be resolved to an unsigned long id
     *
     * @return Possibly-empty unmodifiable list of member for the specified ID
     */
    @Nonnull
    default List<Member> getElementsById(@Nonnull String id)
    {
        return getElementsById(MiscUtil.parseSnowflake(id));
    }

    /**
     * Creates an immutable list of all members matching the given username.
     * <br>This will check the name of the wrapped user.
     *
     * @param  name
     *         The name to check
     * @param  ignoreCase
     *         Whether to ignore case when comparing usernames
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of members with the given username
     */
    @Nonnull
    List<Member> getElementsByUsername(@Nonnull String name, boolean ignoreCase);

    /**
     * Creates an immutable list of all members matching the given username.
     * <br>This will check the name of the wrapped user.
     *
     * @param  name
     *         The name to check
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of members with the given username
     */
    @Nonnull
    default List<Member> getElementsByUsername(@Nonnull String name)
    {
        return getElementsByUsername(name, false);
    }

    /**
     * Creates an immutable list of all members matching the given nickname.
     * <br>This will check the nickname of the member.
     * If provided with {@code null} this will check for members
     * that have no nickname set.
     *
     * @param  name
     *         The nullable nickname to check
     * @param  ignoreCase
     *         Whether to ignore case when comparing nicknames
     *
     * @return Immutable list of members with the given nickname
     */
    @Nonnull
    List<Member> getElementsByNickname(@Nullable String name, boolean ignoreCase);

    /**
     * Creates an immutable list of all members matching the given nickname.
     * <br>This will check the nickname of the member.
     * If provided with {@code null} this will check for members
     * that have no nickname set.
     *
     * @param  name
     *         The nullable nickname to check
     *
     * @return Immutable list of members with the given nickname
     */
    @Nonnull
    default List<Member> getElementsByNickname(@Nullable String name)
    {
        return getElementsByNickname(name, false);
    }

    /**
     * Creates an immutable list of all members that hold all
     * of the provided roles.
     *
     * @param  roles
     *         Roles the members should have
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with {@code null}
     *
     * @return Immutable list of members with the given roles
     */
    @Nonnull
    List<Member> getElementsWithRoles(@Nonnull Role... roles);

    /**
     * Creates an immutable list of all members that hold all
     * of the provided roles.
     *
     * @param  roles
     *         Roles the members should have
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with {@code null}
     *
     * @return Immutable list of members with the given roles
     */
    @Nonnull
    List<Member> getElementsWithRoles(@Nonnull Collection<Role> roles);
}
