package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public enum EmbedType {
    IMAGE("image"),
    VIDEO("video"),
    LINK("link"),
    RICH("rich"),
    UNKNOWN("");

    private final String key;

    EmbedType(String key) {
        this.key = key;
    }


    @Nonnull
    public static EmbedType fromKey(String key) {
        for (EmbedType type : values()) {
            if (type.key.equals(key))
                return type;
        }
        return UNKNOWN;
    }
}
