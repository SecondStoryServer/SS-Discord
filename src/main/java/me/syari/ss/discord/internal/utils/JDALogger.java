package me.syari.ss.discord.internal.utils;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.ServiceLoader;

public class JDALogger {
    public static final boolean SLF4J_ENABLED;

    static {
        boolean tmp;
        try {
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            tmp = true;
        } catch (ClassNotFoundException eStatic) {
            try {
                Class<?> serviceProviderInterface = Class.forName("org.slf4j.spi.SLF4JServiceProvider");
                tmp = ServiceLoader.load(serviceProviderInterface).iterator().hasNext();
            } catch (ClassNotFoundException eService) {
                LoggerFactory.getLogger(JDALogger.class);
                tmp = false;
            }
        }
        SLF4J_ENABLED = tmp;
    }

    private static final Map<String, Logger> LOGS = new CaseInsensitiveMap<>();

    public static Logger getLog(Class<?> clazz) {
        synchronized (LOGS) {
            if (SLF4J_ENABLED)
                return LoggerFactory.getLogger(clazz);
            return LOGS.computeIfAbsent(clazz.getName(), (n) -> new SimpleLogger(clazz.getSimpleName()));
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Object getLazyString(LazyEvaluation lazyLambda) {
        return new Object() {
            @Override
            public String toString() {
                try {
                    return lazyLambda.getString();
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    return "Error while evaluating lazy String... " + sw.toString();
                }
            }
        };
    }

    @FunctionalInterface
    public interface LazyEvaluation {
        String getString() throws Exception;
    }
}

