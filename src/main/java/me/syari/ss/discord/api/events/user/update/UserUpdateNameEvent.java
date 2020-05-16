package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public class UserUpdateNameEvent extends GenericUserUpdateEvent<String> {
    public static final String IDENTIFIER = "name";

    public UserUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull String oldName) {
        super(api, responseNumber, user, oldName, user.getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName() {
        return getOldValue();
    }


    @Nonnull
    public String getNewName() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue() {
        return super.getNewValue();
    }
}
