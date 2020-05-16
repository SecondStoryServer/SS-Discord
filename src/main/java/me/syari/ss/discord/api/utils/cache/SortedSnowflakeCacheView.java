package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;

import javax.annotation.Nonnull;
import java.util.stream.Stream;


public interface SortedSnowflakeCacheView<T extends Comparable<? super T> & ISnowflake> extends SnowflakeCacheView<T> {


    @Nonnull
    Stream<T> streamUnordered();


    @Nonnull
    Stream<T> parallelStreamUnordered();
}
