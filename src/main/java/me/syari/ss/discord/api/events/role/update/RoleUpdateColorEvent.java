package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;


public class RoleUpdateColorEvent extends GenericRoleUpdateEvent<Integer> {
    public static final String IDENTIFIER = "color";

    public RoleUpdateColorEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldColor) {
        super(api, responseNumber, role, oldColor, role.getColorRaw(), IDENTIFIER);
    }


    @Nullable
    public Color getOldColor() {
        return previous != Role.DEFAULT_COLOR_RAW ? new Color(previous) : null;
    }


    public int getOldColorRaw() {
        return getOldValue();
    }


    @Nullable
    public Color getNewColor() {
        return next != Role.DEFAULT_COLOR_RAW ? new Color(next) : null;
    }


    public int getNewColorRaw() {
        return getNewValue();
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
