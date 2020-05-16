package me.syari.ss.discord.api.events.emote;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericEmoteEvent extends Event {
    protected final Emote emote;

    public GenericEmoteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote) {
        super(api, responseNumber);
        this.emote = emote;
    }


    @Nonnull
    public Guild getGuild() {
        return emote.getGuild();
    }


    @Nonnull
    public Emote getEmote() {
        return emote;
    }


    public boolean isManaged() {
        return emote.isManaged();
    }
}
