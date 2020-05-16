package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class ChannelActionImpl<T extends GuildChannel> extends AuditableRestActionImpl<T> implements ChannelAction<T> {
    protected final Set<PermOverrideData> overrides = new HashSet<>();
    protected final Guild guild;
    protected final ChannelType type;
    protected final Class<T> clazz;
    protected String name;
    protected Category parent;
    protected Integer position;

    // --text only--
    protected String topic = null;
    protected Boolean nsfw = null;
    protected Integer slowmode = null;

    // --voice only--
    protected Integer bitrate = null;
    protected Integer userlimit = null;

    public ChannelActionImpl(Class<T> clazz, String name, Guild guild, ChannelType type) {
        super(guild.getJDA(), Route.Guilds.CREATE_CHANNEL.compile(guild.getId()));
        this.clazz = clazz;
        this.guild = guild;
        this.type = type;
        this.name = name;
    }

    @Nonnull
    @Override
    public ChannelActionImpl<T> setCheck(BooleanSupplier checks) {
        return (ChannelActionImpl<T>) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return guild;
    }

    @Nonnull
    @Override
    public ChannelType getType() {
        return type;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setName(@Nonnull String name) {
        Checks.notNull(name, "Channel name");
        if (name.length() < 1 || name.length() > 100)
            throw new IllegalArgumentException("Provided channel name must be 1 to 100 characters in length");

        this.name = name;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setParent(Category category) {
        Checks.check(category == null || category.getGuild().equals(guild), "Category is not from same guild!");
        this.parent = category;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setPosition(Integer position) {
        Checks.check(position == null || position >= 0, "Position must be >= 0!");
        this.position = position;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setTopic(String topic) {
        if (type != ChannelType.TEXT)
            throw new UnsupportedOperationException("Can only set the topic for a TextChannel!");
        if (topic != null && topic.length() > 1024)
            throw new IllegalArgumentException("Channel Topic must not be greater than 1024 in length!");
        this.topic = topic;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setNSFW(boolean nsfw) {
        if (type != ChannelType.TEXT)
            throw new UnsupportedOperationException("Can only set nsfw for a TextChannel!");
        this.nsfw = nsfw;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setSlowmode(int slowmode) {
        if (type != ChannelType.TEXT)
            throw new UnsupportedOperationException("Can only set slowmode on text channels");
        Checks.check(slowmode <= TextChannel.MAX_SLOWMODE && slowmode >= 0, "Slowmode must be between 0 and %d (seconds)!", TextChannel.MAX_SLOWMODE);
        this.slowmode = slowmode;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> addPermissionOverride(@Nonnull IPermissionHolder target, long allow, long deny) {
        Checks.notNull(target, "Override Role");
        Checks.notNegative(allow, "Granted permissions value");
        Checks.notNegative(deny, "Denied permissions value");
        Checks.check(allow <= Permission.ALL_PERMISSIONS, "Specified allow value may not be greater than a full permission set");
        Checks.check(deny <= Permission.ALL_PERMISSIONS, "Specified deny value may not be greater than a full permission set");
        Checks.check(target.getGuild().equals(guild), "Specified Role is not in the same Guild!");

        if (target instanceof Role) {
            Role r = (Role) target;
            long id = r.getIdLong();
            overrides.add(new PermOverrideData(PermOverrideData.ROLE_TYPE, id, allow, deny));
        } else {
            Member m = (Member) target;
            long id = m.getUser().getIdLong();
            overrides.add(new PermOverrideData(PermOverrideData.MEMBER_TYPE, id, allow, deny));
        }
        return this;
    }

    // --voice only--
    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setBitrate(Integer bitrate) {
        if (bitrate != null) {
            int maxBitrate = getGuild().getMaxBitrate();
            if (bitrate < 8000)
                throw new IllegalArgumentException("Bitrate must be greater than 8000.");
            else if (bitrate > maxBitrate)
                throw new IllegalArgumentException("Bitrate must be less than " + maxBitrate);
        }

        this.bitrate = bitrate;
        return this;
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelActionImpl<T> setUserlimit(Integer userlimit) {
        if (userlimit != null && (userlimit < 0 || userlimit > 99))
            throw new IllegalArgumentException("Userlimit must be between 0-99!");
        this.userlimit = userlimit;
        return this;
    }

    @Override
    protected RequestBody finalizeData() {
        DataObject object = DataObject.empty();
        object.put("name", name);
        object.put("type", type.getId());
        object.put("permission_overwrites", DataArray.fromCollection(overrides));
        if (position != null)
            object.put("position", position);
        if (type == ChannelType.TEXT) {
            if (topic != null && !topic.isEmpty())
                object.put("topic", topic);
            if (nsfw != null)
                object.put("nsfw", nsfw);
            if (slowmode != null)
                object.put("rate_limit_per_user", slowmode);
        }
        if (type != ChannelType.CATEGORY && parent != null)
            object.put("parent_id", parent.getId());

        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(Response response, Request<T> request) {
        EntityBuilder builder = api.getEntityBuilder();
        GuildChannel channel;
        switch (type) {
            case TEXT:
                channel = builder.createTextChannel(response.getObject(), guild.getIdLong());
                break;
            case CATEGORY:
                channel = builder.createCategory(response.getObject(), guild.getIdLong());
                break;
            default:
                request.onFailure(new IllegalStateException("Created channel of unknown type!"));
                return;
        }
        request.onSuccess(clazz.cast(channel));
    }

    protected void checkPermissions(Collection<Permission> permissions) {
        if (permissions == null)
            return;
        for (Permission p : permissions)
            Checks.notNull(p, "Permissions");
    }
}
