package me.syari.ss.discord.api;

public enum OnlineStatus {

    ONLINE("online"),

    IDLE("idle"),

    DO_NOT_DISTURB("dnd"),

    INVISIBLE("invisible"),

    OFFLINE("offline"),

    UNKNOWN("");

    private final String key;

    OnlineStatus(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


}
