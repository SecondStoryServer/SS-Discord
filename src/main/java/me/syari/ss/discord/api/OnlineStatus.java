package me.syari.ss.discord.api;

public enum OnlineStatus {

    ONLINE("online"),

    INVISIBLE("invisible"),

    OFFLINE("offline");

    private final String key;

    OnlineStatus(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


}
