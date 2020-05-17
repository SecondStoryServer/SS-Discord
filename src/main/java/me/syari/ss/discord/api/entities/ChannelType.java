package me.syari.ss.discord.api.entities;

public class ChannelType {
    public static boolean isTextChannel(int id) {
        return id == 0;
    }
}
