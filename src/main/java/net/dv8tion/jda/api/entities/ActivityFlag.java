

package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Enum representing the flags in a {@link net.dv8tion.jda.api.entities.RichPresence RichPresence}
 */
public enum ActivityFlag
{
    INSTANCE(0),
    JOIN(1),
    SPECTATE(2),
    JOIN_REQUEST(3),
    SYNC(4),
    PLAY(5);

    private final int offset;
    private final int raw;

    ActivityFlag(int offset)
    {
        this.offset = offset;
        this.raw = 1 << offset;
    }

    /**
     * The offset for this flag: {@code 1 << offset}
     *
     * @return The offset
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * The raw bitmask for this flag
     *
     * @return The raw bitmask
     */
    public int getRaw()
    {
        return raw;
    }

    /**
     * Maps the ActivityFlags based on the provided bitmask.
     *
     * @param  raw
     *         The bitmask
     *
     * @return EnumSet containing the set activity flags
     *
     * @see    RichPresence#getFlags()
     * @see    EnumSet EnumSet
     */
    @Nonnull
    public static EnumSet<ActivityFlag> getFlags(int raw)
    {
        EnumSet<ActivityFlag> set = EnumSet.noneOf(ActivityFlag.class);
        if (raw == 0)
            return set;
        for (ActivityFlag flag : values())
        {
            if ((flag.getRaw() & raw) == flag.getRaw())
                set.add(flag);
        }
        return set;
    }
}
