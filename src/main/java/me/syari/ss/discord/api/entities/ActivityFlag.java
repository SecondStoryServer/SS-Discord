

package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;
import java.util.EnumSet;


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


    public int getOffset()
    {
        return offset;
    }


    public int getRaw()
    {
        return raw;
    }


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
