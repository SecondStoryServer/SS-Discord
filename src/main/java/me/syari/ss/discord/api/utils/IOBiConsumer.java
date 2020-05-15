

package me.syari.ss.discord.api.utils;

import java.io.IOException;

@FunctionalInterface
public interface IOBiConsumer<T, R>
{
    void accept(T a, R b) throws IOException;
}
