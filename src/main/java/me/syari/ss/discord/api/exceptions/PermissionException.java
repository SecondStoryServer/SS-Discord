package me.syari.ss.discord.api.exceptions;

public class PermissionException extends RuntimeException {
    protected PermissionException(String reason) {
        super(reason);
    }
}
