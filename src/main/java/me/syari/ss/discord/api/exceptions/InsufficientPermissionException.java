

package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InsufficientPermissionException extends PermissionException
{
    private final long guildId;
    private final long channelId;
    private final ChannelType channelType;

    public InsufficientPermissionException(@Nonnull Guild guild, @Nonnull Permission permission)
    {
        this(guild, null, permission);
    }

    public InsufficientPermissionException(@Nonnull Guild guild, @Nonnull Permission permission, @Nonnull String reason)
    {
        this(guild, null, permission, reason);
    }

    public InsufficientPermissionException(@Nonnull GuildChannel channel, @Nonnull Permission permission)
    {
        this(channel.getGuild(), channel, permission);
    }

    public InsufficientPermissionException(@Nonnull GuildChannel channel, @Nonnull Permission permission, @Nonnull String reason)
    {
        this(channel.getGuild(), channel, permission, reason);
    }

    private InsufficientPermissionException(@Nonnull Guild guild, @Nullable GuildChannel channel, @Nonnull Permission permission)
    {
        super(permission, "Cannot perform action due to a lack of Permission. Missing permission: " + permission.toString());
        this.guildId = guild.getIdLong();
        this.channelId = channel == null ? 0 : channel.getIdLong();
        this.channelType = channel == null ? ChannelType.UNKNOWN : channel.getType();
    }

    private InsufficientPermissionException(@Nonnull Guild guild, @Nullable GuildChannel channel, @Nonnull Permission permission, @Nonnull String reason)
    {
        super(permission, reason);
        this.guildId = guild.getIdLong();
        this.channelId = channel == null ? 0 : channel.getIdLong();
        this.channelType = channel == null ? ChannelType.UNKNOWN : channel.getType();
    }

    /**
     * The id for the responsible {@link Guild} instance.
     *
     * @return The ID as a long
     *
     * @since  4.0.0
     *
     * @see    JDA#getGuildById(long)
     */
    public long getGuildId()
    {
        return guildId;
    }

    /**
     * The id for the responsible {@link GuildChannel} instance.
     *
     * @return The ID as a long or 0
     *
     * @since  4.0.0
     *
     * @see    #getChannel(JDA)
     */
    public long getChannelId()
    {
        return channelId;
    }

    /**
     * The {@link ChannelType} for the {@link #getChannelId() channel id}.
     *
     * @return The channel type or {@link ChannelType#UNKNOWN}.
     *
     * @since  4.0.0
     */
    @Nonnull
    public ChannelType getChannelType()
    {
        return channelType;
    }

    /**
     * The {@link Guild} instance for the {@link #getGuildId() guild id}.
     *
     * @param  api
     *         The shard to perform the lookup in
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided JDA instance is null
     *
     * @since  4.0.0
     *
     * @return The Guild instance or null
     */
    @Nullable
    public Guild getGuild(@Nonnull JDA api)
    {
        Checks.notNull(api, "JDA");
        return api.getGuildById(guildId);
    }

    /**
     * The {@link GuildChannel} instance for the {@link #getChannelId() channel id}.
     *
     * @param  api
     *         The shard to perform the lookup in
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided JDA instance is null
     *
     * @since  4.0.0
     *
     * @return The GuildChannel instance or null
     */
    @Nullable
    public GuildChannel getChannel(@Nonnull JDA api)
    {
        Checks.notNull(api, "JDA");
        return api.getGuildChannelById(channelType, channelId);
    }
}
