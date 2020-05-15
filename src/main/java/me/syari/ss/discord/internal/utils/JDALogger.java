

package me.syari.ss.discord.internal.utils;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.ServiceLoader;


public class JDALogger
{

    public static final boolean SLF4J_ENABLED;
    static
    {
        boolean tmp = false;

        try
        {
            Class.forName("org.slf4j.impl.StaticLoggerBinder");

            tmp = true;
        }
        catch (ClassNotFoundException eStatic)
        {
            // there was no static logger binder (SLF4J pre-1.8.x)

            try
            {
                Class<?> serviceProviderInterface = Class.forName("org.slf4j.spi.SLF4JServiceProvider");

                // check if there is a service implementation for the service, indicating a provider for SLF4J 1.8.x+ is installed
                tmp = ServiceLoader.load(serviceProviderInterface).iterator().hasNext();
            }
            catch (ClassNotFoundException eService)
            {
                // there was no service provider interface (SLF4J 1.8.x+)

                //prints warning of missing implementation
                LoggerFactory.getLogger(JDALogger.class);

                tmp = false;
            }
        }

        SLF4J_ENABLED = tmp;
    }

    private static final Map<String, Logger> LOGS = new CaseInsensitiveMap<>();

    private JDALogger() {}


    public static Logger getLog(String name)
    {
        synchronized (LOGS)
        {
            if (SLF4J_ENABLED)
                return LoggerFactory.getLogger(name);
            return LOGS.computeIfAbsent(name, SimpleLogger::new);
        }
    }


    public static Logger getLog(Class<?> clazz)
    {
        synchronized (LOGS)
        {
            if (SLF4J_ENABLED)
                return LoggerFactory.getLogger(clazz);
            return LOGS.computeIfAbsent(clazz.getName(), (n) -> new SimpleLogger(clazz.getSimpleName()));
        }
    }


    public static Object getLazyString(LazyEvaluation lazyLambda)
    {
        return new Object()
        {
            @Override
            public String toString()
            {
                try
                {
                    return lazyLambda.getString();
                }
                catch (Exception ex)
                {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    return "Error while evaluating lazy String... " + sw.toString();
                }
            }
        };
    }


    @FunctionalInterface
    public interface LazyEvaluation
    {

        String getString() throws Exception;
    }
}

