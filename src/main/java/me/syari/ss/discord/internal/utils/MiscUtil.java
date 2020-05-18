package me.syari.ss.discord.internal.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class MiscUtil {
    public static long parseSnowflake(@NotNull String input) {
        try {
            if (input.startsWith("-")) {
                return Long.parseLong(input);
            } else {
                return Long.parseUnsignedLong(input);
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(String.format("The specified ID is not a valid snowflake (%s). Expecting a valid long value!", input));
        }
    }

    public static <E> E locked(@NotNull ReentrantLock lock, @NotNull Supplier<E> task) {
        try {
            lock.lockInterruptibly();
            return task.get();
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public static void locked(@NotNull ReentrantLock lock, @NotNull Runnable task) {
        try {
            lock.lockInterruptibly();
            task.run();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
