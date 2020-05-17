package me.syari.ss.discord.api;

public enum OnlineStatus {

    ONLINE("online");

    private final String key;

    OnlineStatus(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


}
