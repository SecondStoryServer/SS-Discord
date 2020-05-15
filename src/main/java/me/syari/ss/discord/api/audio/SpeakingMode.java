

package me.syari.ss.discord.api.audio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;


public enum SpeakingMode
{
    VOICE(1), SOUNDSHARE(2), PRIORITY(4);

    private final int raw;

    SpeakingMode(int raw)
    {
        this.raw = raw;
    }


    public int getRaw()
    {
        return raw;
    }


    @Nonnull
    public static EnumSet<SpeakingMode> getModes(int mask)
    {
        final EnumSet<SpeakingMode> modes = EnumSet.noneOf(SpeakingMode.class);
        if (mask == 0)
            return modes;
        final SpeakingMode[] values = SpeakingMode.values();
        for (SpeakingMode mode : values)
        {
            if ((mode.raw & mask) == mode.raw)
                modes.add(mode);
        }
        return modes;
    }


    public static int getRaw(@Nullable SpeakingMode... modes)
    {
        if (modes == null || modes.length == 0)
            return 0;
        int mask = 0;
        for (SpeakingMode m : modes)
            mask |= m.raw;
        return mask;
    }


    public static int getRaw(@Nullable Collection<SpeakingMode> modes)
    {
        if (modes == null)
            return 0;
        int raw = 0;
        for (SpeakingMode mode : modes)
            raw |= mode.getRaw();
        return raw;
    }
}
