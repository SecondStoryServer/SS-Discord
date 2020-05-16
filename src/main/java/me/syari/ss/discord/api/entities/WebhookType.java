package me.syari.ss.discord.api.entities;

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


}
