package me.syari.ss.discord.api.entities;

public class MessageType {
    public static boolean isDefaultMessage(int id) {
        return id == 0;
    }
}
