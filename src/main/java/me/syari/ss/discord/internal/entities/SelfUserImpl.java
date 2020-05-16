package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.internal.JDAImpl;

public class SelfUserImpl extends UserImpl implements User {
    public SelfUserImpl(long id, JDAImpl api) {
        super(id, api);
    }
}
