package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public class RoleUpdatePositionEvent extends GenericRoleUpdateEvent<Integer> {
    public static final String IDENTIFIER = "position";

    private final int oldPositionRaw;
    private final int newPositionRaw;

    public RoleUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldPosition, int oldPositionRaw) {
        super(api, responseNumber, role, oldPosition, role.getPosition(), IDENTIFIER);
        this.oldPositionRaw = oldPositionRaw;
        this.newPositionRaw = role.getPositionRaw();
    }


    public int getOldPosition() {
        return getOldValue();
    }


    public int getOldPositionRaw() {
        return oldPositionRaw;
    }


    public int getNewPosition() {
        return getNewValue();
    }


    public int getNewPositionRaw() {
        return newPositionRaw;
    }

    @Nonnull
    @Override
    public Integer getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Integer getNewValue() {
        return super.getNewValue();
    }
}
