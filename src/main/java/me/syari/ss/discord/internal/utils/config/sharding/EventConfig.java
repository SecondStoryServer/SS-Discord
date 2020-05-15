

package me.syari.ss.discord.internal.utils.config.sharding;

import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.hooks.IEventManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class EventConfig
{
    private final List<Object> listeners = new ArrayList<>();
    private final List<IntFunction<Object>> listenerProviders = new ArrayList<>();
    private final IntFunction<? extends IEventManager> eventManagerProvider;

    public EventConfig(@Nullable IntFunction<? extends IEventManager> eventManagerProvider)
    {
        this.eventManagerProvider = eventManagerProvider;
    }

    public void addEventListener(@Nonnull Object listener)
    {
        Checks.notNull(listener, "Listener");
        listeners.add(listener);
    }

    public void removeEventListener(@Nonnull Object listener)
    {
        Checks.notNull(listener, "Listener");
        listeners.remove(listener);
    }

    public void addEventListenerProvider(@Nonnull IntFunction<Object> provider)
    {
        Checks.notNull(provider, "Provider");
        listenerProviders.add(provider);
    }

    public void removeEventListenerProvider(@Nonnull IntFunction<Object> provider)
    {
        Checks.notNull(provider, "Provider");
        listenerProviders.remove(provider);
    }

    @Nonnull
    public List<Object> getListeners()
    {
        return listeners;
    }

    @Nonnull
    public List<IntFunction<Object>> getListenerProviders()
    {
        return listenerProviders;
    }

    @Nullable
    public IntFunction<? extends IEventManager> getEventManagerProvider()
    {
        return eventManagerProvider;
    }

    @Nonnull
    public static EventConfig getDefault()
    {
        return new EventConfig(null);
    }
}
