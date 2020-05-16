package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.api.utils.data.SerializableData;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;


public interface GuildAction extends RestAction<Void> {
    @Nonnull
    @Override
    GuildAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    @CheckReturnValue
    GuildAction setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    ChannelData getChannel(int index);


    class RoleData implements SerializableData {
        protected final long id;
        protected final boolean isPublicRole;

        protected Long permissions;
        protected String name;
        protected Integer color;
        protected Integer position;
        protected Boolean mentionable, hoisted;

        public RoleData(long id) {
            this.id = id;
            this.isPublicRole = id == 0;
        }


        @Nonnull
        public RoleData setName(@Nullable String name) {
            checkPublic();
            this.name = name;
            return this;
        }


        @Nonnull
        @Override
        public DataObject toData() {
            final DataObject o = DataObject.empty().put("id", Long.toUnsignedString(id));
            if (permissions != null)
                o.put("permissions", permissions);
            if (position != null)
                o.put("position", position);
            if (name != null)
                o.put("name", name);
            if (color != null)
                o.put("color", color & 0xFFFFFF);
            if (mentionable != null)
                o.put("mentionable", mentionable);
            if (hoisted != null)
                o.put("hoist", hoisted);
            return o;
        }

        protected void checkPublic() {
            if (isPublicRole)
                throw new IllegalStateException("Cannot modify " + "name" + " for the public role!");
        }
    }


    class ChannelData implements SerializableData {
        protected final ChannelType type;
        protected final String name;

        protected Integer position;

        // Text only
        protected String topic;
        protected Boolean nsfw;
        // Voice only
        protected Integer bitrate, userlimit;


        public ChannelData(ChannelType type, String name) {
            Checks.notBlank(name, "Name");
            Checks.check(name.length() >= 2 && name.length() <= 100, "Channel name has to be between 2-100 characters long!");

            this.type = type;
            this.name = name;
        }


        @Nonnull
        @Override
        public DataObject toData() {
            final DataObject o = DataObject.empty();
            o.put("name", name);
            o.put("type", type.getId());
            if (topic != null)
                o.put("topic", topic);
            if (nsfw != null)
                o.put("nsfw", nsfw);
            if (bitrate != null)
                o.put("bitrate", bitrate);
            if (userlimit != null)
                o.put("user_limit", userlimit);
            if (position != null)
                o.put("position", position);
            return o;
        }
    }
}
