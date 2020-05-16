package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class ResumedEvent extends Event {
    public ResumedEvent(@Nonnull JDA api, long responseNumber) {
        super(api, responseNumber);
    }
}
