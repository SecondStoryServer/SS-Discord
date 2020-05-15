
package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.events.Event;
import me.syari.ss.discord.api.events.GenericEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class AnnotatedEventManager implements IEventManager
{
    private final Set<Object> listeners = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, Map<Object, List<Method>>> methods = new ConcurrentHashMap<>();

    @Override
    public void register(@Nonnull Object listener)
    {
        if (listeners.add(listener))
        {
            updateMethods();
        }
    }

    @Override
    public void unregister(@Nonnull Object listener)
    {
        if (listeners.remove(listener))
        {
            updateMethods();
        }
    }

    @Nonnull
    @Override
    public List<Object> getRegisteredListeners()
    {
        return Collections.unmodifiableList(new ArrayList<>(listeners));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(@Nonnull GenericEvent event)
    {
        Class<?> eventClass = event.getClass();
        do
        {
            Map<Object, List<Method>> listeners = methods.get(eventClass);
            if (listeners != null)
            {
                listeners.forEach((key, value) -> value.forEach(method ->
                {
                    try
                    {
                        method.setAccessible(true);
                        method.invoke(key, event);
                    }
                    catch (IllegalAccessException | InvocationTargetException e1)
                    {
                        JDAImpl.LOG.error("Couldn't access annotated EventListener method", e1);
                    }
                    catch (Throwable throwable)
                    {
                        JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
                    }
                }));
            }
            eventClass = eventClass == Event.class ? null : (Class<? extends GenericEvent>) eventClass.getSuperclass();
        }
        while (eventClass != null);
    }

    private void updateMethods()
    {
        methods.clear();
        for (Object listener : listeners)
        {
            boolean isClass = listener instanceof Class;
            Class<?> c = isClass ? (Class) listener : listener.getClass();
            Method[] allMethods = c.getDeclaredMethods();
            for (Method m : allMethods)
            {
                if (!m.isAnnotationPresent(SubscribeEvent.class) || (isClass && !Modifier.isStatic(m.getModifiers())))
                {
                    continue;
                }
                Class<?>[] pType  = m.getParameterTypes();
                if (pType.length == 1 && GenericEvent.class.isAssignableFrom(pType[0]))
                {
                    Class<?> eventClass = pType[0];
                    if (!methods.containsKey(eventClass))
                    {
                        methods.put(eventClass, new ConcurrentHashMap<>());
                    }

                    if (!methods.get(eventClass).containsKey(listener))
                    {
                        methods.get(eventClass).put(listener, new CopyOnWriteArrayList<>());
                    }

                    methods.get(eventClass).get(listener).add(m);
                }
            }
        }
    }
}
