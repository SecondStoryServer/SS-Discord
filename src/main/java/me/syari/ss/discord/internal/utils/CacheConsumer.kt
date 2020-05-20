package me.syari.ss.discord.internal.utils;

import me.syari.ss.discord.api.utils.data.DataObject;

@FunctionalInterface
public interface CacheConsumer {
    void execute(long responseTotal, DataObject allContent);
}
