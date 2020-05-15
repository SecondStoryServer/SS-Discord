

package me.syari.ss.discord.api.utils;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T>
{
    void accept(T t) throws IOException;
}
