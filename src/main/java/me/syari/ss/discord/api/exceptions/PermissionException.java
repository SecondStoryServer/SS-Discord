package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.Permission;


public class PermissionException extends RuntimeException {
    private final Permission permission;


    public PermissionException(String reason) {
        this(Permission.UNKNOWN, reason);
    }


    protected PermissionException(Permission permission, String reason) {
        super(reason);
        this.permission = permission;
    }


    public Permission getPermission() {
        return permission;
    }
}
