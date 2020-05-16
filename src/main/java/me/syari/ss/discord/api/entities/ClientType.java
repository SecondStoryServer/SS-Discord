package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;

public enum ClientType {

    DESKTOP("desktop"),

    MOBILE("mobile"),

    WEB("web"),

    UNKNOWN("unknown"),
    ;

    private final String key;

    ClientType(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


    @Nonnull
    public static ClientType fromKey(@Nonnull String key) {
        for (ClientType type : values()) {
            if (type.key.equals(key))
                return type;
        }
        return UNKNOWN;
    }
}
