package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.channel.voice.GenericVoiceChannelEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericVoiceChannelUpdateEvent<T> extends GenericVoiceChannelEvent implements UpdateEvent<VoiceChannel, T> {
    private final String identifier;
    private final T prev;
    private final T next;

    public GenericVoiceChannelUpdateEvent(
            @Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel,
            @Nullable T prev, @Nullable T next, @Nonnull String identifier) {
        super(api, responseNumber, channel);
        this.prev = prev;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public VoiceChannel getEntity() {
        return getChannel();
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier() {
        return identifier;
    }

    @Nullable
    @Override
    public T getOldValue() {
        return prev;
    }

    @Nullable
    @Override
    public T getNewValue() {
        return next;
    }

    @Override
    public String toString() {
        return "VoiceChannelUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
