
package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Enum used to differentiate between the different types of Discord channels.
 */
public enum ChannelType
{
    /**
     * A {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}, Guild-Only.
     */
    TEXT(0, 0, true),
    /**
     * A {@link net.dv8tion.jda.api.entities.PrivateChannel PrivateChannel}.
     */
    PRIVATE(1, -1),
    /**
     * A {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel}, Guild-Only.
     */
    VOICE(2, 1, true),
    /**
     * A Group. {@link net.dv8tion.jda.api.AccountType#CLIENT AccountType.CLIENT} only.
     */
    GROUP(3, -1),
    /**
     * A {@link net.dv8tion.jda.api.entities.Category Category}, Guild-Only.
     */
    CATEGORY(4, 2, true),
    /**
     * A {@link net.dv8tion.jda.api.entities.StoreChannel StoreChannel}, Guild-Only.
     */
    STORE(6, 0, true),
    /**
     * Unknown Discord channel type. Should never happen and would only possibly happen if Discord implemented a new
     * channel type and JDA had yet to implement support for it.
     */
    UNKNOWN(-1, -2);

    private final int sortBucket;
    private final int id;
    private final boolean isGuild;

    ChannelType(int id, int sortBucket)
    {
        this(id, sortBucket, false);
    }

    ChannelType(int id, int sortBucket, boolean isGuild)
    {
        this.id = id;
        this.sortBucket = sortBucket;
        this.isGuild = isGuild;
    }

    /**
     * The sorting bucket for this channel type.
     *
     * @return The sorting bucket
     */
    public int getSortBucket()
    {
        return sortBucket;
    }

    /**
     * The Discord id key used to represent the channel type.
     *
     * @return The id key used by discord for this channel type.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Whether this ChannelType is present for a {@link GuildChannel GuildChannel}
     *
     * @return Whether or not this a GuildChannel
     */
    public boolean isGuild()
    {
        return isGuild;
    }

    /**
     * Static accessor for retrieving a channel type based on its Discord id key.
     *
     * @param  id
     *         The id key of the requested channel type.
     *
     * @return The ChannelType that is referred to by the provided key. If the id key is unknown, {@link #UNKNOWN} is returned.
     */
    @Nonnull
    public static ChannelType fromId(int id)
    {
        if (id == 5) // NEWS = TEXT
            return TEXT;
        for (ChannelType type : values())
        {
            if (type.id == id)
                return type;
        }
        return UNKNOWN;
    }

    /**
     * An {@link java.util.EnumSet} populated with all channel types using the provided sorting bucket.
     *
     * @param  bucket
     *         The sorting bucket
     *
     * @return Possibly-empty {@link java.util.EnumSet} for the bucket
     */
    @Nonnull
    public static EnumSet<ChannelType> fromSortBucket(int bucket)
    {
        EnumSet<ChannelType> types = EnumSet.noneOf(ChannelType.class);
        for (ChannelType type : values())
        {
            if (type.getSortBucket() == bucket)
                types.add(type);
        }
        return types;
    }
}
