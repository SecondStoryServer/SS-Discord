

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.api.utils.data.SerializableData;
import me.syari.ss.discord.internal.requests.restaction.GuildActionImpl;
import me.syari.ss.discord.internal.requests.restaction.PermOverrideData;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;


public interface GuildAction extends RestAction<Void>
{
    @Nonnull
    @Override
    GuildAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    @CheckReturnValue
    GuildAction setRegion(@Nullable Region region);


    @Nonnull
    @CheckReturnValue
    GuildAction setIcon(@Nullable Icon icon);


    @Nonnull
    @CheckReturnValue
    GuildAction setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    GuildAction setVerificationLevel(@Nullable Guild.VerificationLevel level);


    @Nonnull
    @CheckReturnValue
    GuildAction setNotificationLevel(@Nullable Guild.NotificationLevel level);


    @Nonnull
    @CheckReturnValue
    GuildAction setExplicitContentLevel(@Nullable Guild.ExplicitContentLevel level);


    @Nonnull
    @CheckReturnValue
    GuildAction addChannel(@Nonnull ChannelData channel);


    @Nonnull
    @CheckReturnValue
    ChannelData getChannel(int index);


    @Nonnull
    @CheckReturnValue
    ChannelData removeChannel(int index);


    @Nonnull
    @CheckReturnValue
    GuildAction removeChannel(@Nonnull ChannelData data);


    @Nonnull
    @CheckReturnValue
    ChannelData newChannel(@Nonnull ChannelType type, @Nonnull String name);


    @Nonnull
    @CheckReturnValue
    RoleData getPublicRole();


    @Nonnull
    @CheckReturnValue
    RoleData getRole(int index);


    @Nonnull
    @CheckReturnValue
    RoleData newRole();


    class RoleData implements SerializableData
    {
        protected final long id;
        protected final boolean isPublicRole;

        protected Long permissions;
        protected String name;
        protected Integer color;
        protected Integer position;
        protected Boolean mentionable, hoisted;

        public RoleData(long id)
        {
            this.id = id;
            this.isPublicRole = id == 0;
        }


        @Nonnull
        public RoleData setPermissionsRaw(@Nullable Long rawPermissions)
        {
            if (rawPermissions != null)
            {
                Checks.notNegative(rawPermissions, "Raw Permissions");
                Checks.check(rawPermissions <= Permission.ALL_PERMISSIONS, "Provided permissions may not be greater than a full permission set!");
            }
            this.permissions = rawPermissions;
            return this;
        }


        @Nonnull
        public RoleData addPermissions(@Nonnull Permission... permissions)
        {
            Checks.notNull(permissions, "Permissions");
            for (Permission perm : permissions)
                Checks.notNull(perm, "Permissions");
            if (this.permissions == null)
                this.permissions = 0L;
            this.permissions |= Permission.getRaw(permissions);
            return this;
        }


        @Nonnull
        public RoleData addPermissions(@Nonnull Collection<Permission> permissions)
        {
            Checks.noneNull(permissions, "Permissions");
            if (this.permissions == null)
                this.permissions = 0L;
            this.permissions |= Permission.getRaw(permissions);
            return this;
        }


        @Nonnull
        public RoleData setName(@Nullable String name)
        {
            checkPublic("name");
            this.name = name;
            return this;
        }


        @Nonnull
        public RoleData setColor(@Nullable Color color)
        {
            checkPublic("color");
            this.color = color == null ? null : color.getRGB();
            return this;
        }


        @Nonnull
        public RoleData setColor(@Nullable Integer color)
        {
            checkPublic("color");
            this.color = color;
            return this;
        }


        @Nonnull
        public RoleData setPosition(@Nullable Integer position)
        {
            checkPublic("position");
            this.position = position;
            return this;
        }


        @Nonnull
        public RoleData setMentionable(@Nullable Boolean mentionable)
        {
            checkPublic("mentionable");
            this.mentionable = mentionable;
            return this;
        }


        @Nonnull
        public RoleData setHoisted(@Nullable Boolean hoisted)
        {
            checkPublic("hoisted");
            this.hoisted = hoisted;
            return this;
        }

        @Nonnull
        @Override
        public DataObject toData()
        {
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

        protected void checkPublic(String comment)
        {
            if (isPublicRole)
                throw new IllegalStateException("Cannot modify " + comment + " for the public role!");
        }
    }


    class ChannelData implements SerializableData
    {
        protected final ChannelType type;
        protected final String name;

        protected final Set<PermOverrideData> overrides = new HashSet<>();

        protected Integer position;

        // Text only
        protected String topic;
        protected Boolean nsfw;
        // Voice only
        protected Integer bitrate, userlimit;


        public ChannelData(ChannelType type, String name)
        {
            Checks.notBlank(name, "Name");
            Checks.check(type == ChannelType.TEXT || type == ChannelType.VOICE, "Can only create channels of type TEXT or VOICE in GuildAction!");
            Checks.check(name.length() >= 2 && name.length() <= 100, "Channel name has to be between 2-100 characters long!");
            Checks.check(type == ChannelType.VOICE || name.matches("[a-zA-Z0-9-_]+"), "Channels of type TEXT must have a name in alphanumeric with underscores!");

            this.type = type;
            this.name = name;
        }


        @Nonnull
        public ChannelData setTopic(@Nullable String topic)
        {
            if (topic != null && topic.length() > 1024)
                throw new IllegalArgumentException("Channel Topic must not be greater than 1024 in length!");
            this.topic = topic;
            return this;
        }


        @Nonnull
        public ChannelData setNSFW(@Nullable Boolean nsfw)
        {
            this.nsfw = nsfw;
            return this;
        }


        @Nonnull
        public ChannelData setBitrate(@Nullable Integer bitrate)
        {
            if (bitrate != null)
            {
                Checks.check(bitrate >= 8000, "Bitrate must be greater than 8000.");
                Checks.check(bitrate <= 96000, "Bitrate must be less than 96000.");
            }
            this.bitrate = bitrate;
            return this;
        }


        @Nonnull
        public ChannelData setUserlimit(@Nullable Integer userlimit)
        {
            if (userlimit != null && (userlimit < 0 || userlimit > 99))
                throw new IllegalArgumentException("Userlimit must be between 0-99!");
            this.userlimit = userlimit;
            return this;
        }


        @Nonnull
        public ChannelData setPosition(@Nullable Integer position)
        {
            this.position = position;
            return this;
        }


        @Nonnull
        public ChannelData addPermissionOverride(@Nonnull GuildActionImpl.RoleData role, long allow, long deny)
        {
            Checks.notNull(role, "Role");
            Checks.notNegative(allow, "Granted permissions value");
            Checks.notNegative(deny, "Denied permissions value");
            Checks.check(allow <= Permission.ALL_PERMISSIONS, "Specified allow value may not be greater than a full permission set");
            Checks.check(deny <= Permission.ALL_PERMISSIONS,  "Specified deny value may not be greater than a full permission set");
            this.overrides.add(new PermOverrideData(PermOverrideData.ROLE_TYPE, role.id, allow, deny));
            return this;
        }


        @Nonnull
        public ChannelData addPermissionOverride(@Nonnull GuildActionImpl.RoleData role, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
        {
            long allowRaw = 0;
            long denyRaw = 0;
            if (allow != null)
            {
                Checks.noneNull(allow, "Granted Permissions");
                allowRaw = Permission.getRaw(allow);
            }
            if (deny != null)
            {
                Checks.noneNull(deny, "Denied Permissions");
                denyRaw = Permission.getRaw(deny);
            }
            return addPermissionOverride(role, allowRaw, denyRaw);
        }

        @Nonnull
        @Override
        public DataObject toData()
        {
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
            if (!overrides.isEmpty())
                o.put("permission_overwrites", overrides);
            return o;
        }
    }
}
