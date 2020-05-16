package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;

public enum WebhookType {

    UNKNOWN(-1),

    INCOMING(1),

    FOLLOWER(2);

    private final int key;

    WebhookType(int key) {
        this.key = key;
    }


    public int getKey() {
        return key;
    }


    @Nonnull
    public static WebhookType fromKey(int key) {
        for (WebhookType type : values()) {
            if (type.key == key)
                return type;
        }
        return UNKNOWN;
    }
}
