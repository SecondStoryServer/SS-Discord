package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public interface Role extends IMentionable, Comparable<Role> {
    @Nonnull
    String getName();
}
