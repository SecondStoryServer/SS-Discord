package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.entities.PermissionOverride;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.api.utils.data.SerializableData;

import javax.annotation.Nonnull;

public class PermOverrideData implements SerializableData {
    public static final int ROLE_TYPE = 0;
    public static final int MEMBER_TYPE = 1;
    public final int type;
    public final long id;
    public final long allow;
    public final long deny;

    public PermOverrideData(int type, long id, long allow, long deny) {
        this.type = type;
        this.id = id;
        this.allow = allow;
        this.deny = deny & ~allow;
    }

    public PermOverrideData(PermissionOverride override) {
        if (override.isMemberOverride()) {
            this.id = override.getMember().getUser().getIdLong();
            this.type = MEMBER_TYPE;
        } else {
            this.id = override.getRole().getIdLong();
            this.type = ROLE_TYPE;
        }
        this.allow = override.getAllowedRaw();
        this.deny = override.getDeniedRaw();
    }

    @Nonnull
    @Override
    public DataObject toData() {
        final DataObject o = DataObject.empty();
        o.put("type", type);
        o.put("id", id);
        o.put("allow", allow);
        o.put("deny", deny);
        return o;
    }
}
