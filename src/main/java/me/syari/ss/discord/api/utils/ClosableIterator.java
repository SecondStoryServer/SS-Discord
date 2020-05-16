package me.syari.ss.discord.api.utils;

import java.util.Iterator;


public interface ClosableIterator<T> extends Iterator<T>, AutoCloseable {
    @Override
    void close();
}
