

package me.syari.ss.discord.internal.utils.config.flags;

import java.util.EnumSet;

public enum ShardingConfigFlag
{
    SHUTDOWN_NOW;

    public static EnumSet<ShardingConfigFlag> getDefault()
    {
        return EnumSet.noneOf(ShardingConfigFlag.class);
    }
}
