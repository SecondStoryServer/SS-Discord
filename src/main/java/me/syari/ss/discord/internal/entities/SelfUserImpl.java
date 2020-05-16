package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.SelfUser;
import me.syari.ss.discord.internal.JDAImpl;

public class SelfUserImpl extends UserImpl implements SelfUser {
    public SelfUserImpl(long id, JDAImpl api) {
        super(id, api);
    }
}
