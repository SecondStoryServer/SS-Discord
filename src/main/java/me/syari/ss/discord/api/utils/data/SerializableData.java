package me.syari.ss.discord.api.utils.data;

import javax.annotation.Nonnull;


public interface SerializableData {

    @Nonnull
    DataObject toData();
}
