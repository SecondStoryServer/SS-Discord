package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class MiscUtil {
    public static long parseSnowflake(String input) {
        Checks.notEmpty(input, "ID");
        try {
            if (!input.startsWith("-"))
                return Long.parseUnsignedLong(input);
            else
                return Long.parseLong(input);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(String.format("The specified ID is not a valid snowflake (%s). Expecting a valid long value!", input));
        }
    }

    public static <E> E locked(@NotNull ReentrantLock lock, @NotNull Supplier<E> task) {
        try {
            lock.lockInterruptibly();
            return task.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            if (lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    public static void locked(@NotNull ReentrantLock lock, @NotNull Runnable task) {
        try {
            lock.lockInterruptibly();
            task.run();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            if (lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

}
