package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public interface Role extends Mentionable, Comparable<Role> {
    @Nonnull
    String getName();
}
