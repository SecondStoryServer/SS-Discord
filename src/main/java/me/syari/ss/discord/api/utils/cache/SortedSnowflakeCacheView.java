package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;


public interface SortedSnowflakeCacheView<T extends Comparable<? super T> & ISnowflake> extends SnowflakeCacheView<T> {


}
