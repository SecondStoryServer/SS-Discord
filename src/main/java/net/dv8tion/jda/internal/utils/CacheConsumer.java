

package net.dv8tion.jda.internal.utils;

import net.dv8tion.jda.api.utils.data.DataObject;

@FunctionalInterface
public interface CacheConsumer
{
    void execute(long responseTotal, DataObject allContent);
}
