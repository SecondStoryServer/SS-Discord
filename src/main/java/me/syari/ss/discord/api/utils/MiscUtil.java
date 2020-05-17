package me.syari.ss.discord.api.utils;

import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Formatter;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class MiscUtil {
    public static <T> TLongObjectMap<T> newLongMap() {
        return new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), new Object());
    }

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


    public static void appendTo(@NotNull Formatter formatter, int width, int precision, boolean leftJustified, String out) {
        try {
            Appendable appendable = formatter.out();
            if (precision > -1 && out.length() > precision) {
                appendable.append(Helpers.truncate(out, precision));
                return;
            }

            if (leftJustified)
                appendable.append(Helpers.rightPad(out, width));
            else
                appendable.append(Helpers.leftPad(out, width));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
