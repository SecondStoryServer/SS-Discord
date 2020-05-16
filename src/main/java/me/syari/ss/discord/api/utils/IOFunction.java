package me.syari.ss.discord.api.utils;

@FunctionalInterface
public interface IOFunction<T, R> {
    R apply(T t);
}
