package me.syari.ss.discord.api.utils;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface Procedure<T> {
    boolean execute(@Nonnull T value);
}
