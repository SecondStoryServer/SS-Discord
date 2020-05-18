package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import org.jetbrains.annotations.NotNull;

public class Role implements ISnowflake {
    private final long id;

    private String name;

    public Role(long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getAsMention() {
        return "<@&" + getId() + '>';
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Role)) {
            return false;
        }
        Role role = (Role) object;
        return this.getIdLong() == role.getIdLong();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "R:" + getName() + '(' + id + ')';
    }

    public void setName(String name) {
        this.name = name;
    }
}
