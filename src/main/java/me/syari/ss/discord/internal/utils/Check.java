package me.syari.ss.discord.internal.utils;

public class Check {
    public static boolean isTextChannel(int id) {
        return id == 0;
    }

    public static boolean isDefaultMessage(int id) {
        return id == 0;
    }
}
