package me.syari.ss.discord.api.utils.data;

import org.jetbrains.annotations.NotNull;

public interface SerializableData {
    @NotNull
    DataObject toData();
}
