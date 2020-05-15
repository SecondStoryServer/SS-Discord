

package net.dv8tion.jda.internal.utils.config.sharding;

import javax.annotation.Nonnull;

public class ShardingConfig
{
    private int shardsTotal;
    private final boolean useShutdownNow;

    public ShardingConfig(int shardsTotal, boolean useShutdownNow)
    {
        this.shardsTotal = shardsTotal;
        this.useShutdownNow = useShutdownNow;
    }

    public void setShardsTotal(int shardsTotal)
    {
        this.shardsTotal = shardsTotal;
    }

    public int getShardsTotal()
    {
        return shardsTotal;
    }

    public boolean isUseShutdownNow()
    {
        return useShutdownNow;
    }

    @Nonnull
    public static ShardingConfig getDefault()
    {
        return new ShardingConfig(1, false);
    }
}
