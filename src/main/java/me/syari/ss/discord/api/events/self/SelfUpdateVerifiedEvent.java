package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateVerifiedEvent extends GenericSelfUpdateEvent<Boolean> {
    public static final String IDENTIFIER = "verified";

    public SelfUpdateVerifiedEvent(@Nonnull JDA api, long responseNumber, boolean wasVerified) {
        super(api, responseNumber, wasVerified, !wasVerified, IDENTIFIER);
    }


    public boolean wasVerified() {
        return getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getNewValue() {
        return super.getNewValue();
    }
}
