

package me.syari.ss.discord.api.utils;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T, R>
{
    R apply(T t) throws IOException;
}
