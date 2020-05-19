package me.syari.ss.discord.api.utils

interface ClosableIterator<T>: MutableIterator<T>, AutoCloseable {
    override fun close()
}