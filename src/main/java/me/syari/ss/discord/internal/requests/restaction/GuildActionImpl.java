package me.syari.ss.discord.internal.requests.restaction;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.requests.restaction.GuildAction;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class GuildActionImpl extends RestActionImpl<Void> implements GuildAction {
    protected String name;
    protected Region region;
    protected Icon icon;
    protected Guild.VerificationLevel verificationLevel;
    protected Guild.NotificationLevel notificationLevel;
    protected Guild.ExplicitContentLevel explicitContentLevel;

    protected final List<RoleData> roles;
    protected final List<ChannelData> channels;

    public GuildActionImpl(JDA api, String name) {
        super(api, Route.Guilds.CREATE_GUILD.compile());
        this.setName(name);

        this.roles = new LinkedList<>();
        this.channels = new LinkedList<>();
        // public role is the first element
        this.roles.add(new RoleData(0));
    }

    @Nonnull
    @Override
    public GuildActionImpl setCheck(BooleanSupplier checks) {
        return (GuildActionImpl) super.setCheck(checks);
    }

    @Nonnull
    @Override
    @CheckReturnValue
    public GuildActionImpl setName(@Nonnull String name) {
        Checks.notBlank(name, "Name");
        name = name.trim();
        Checks.check(name.length() >= 2 && name.length() <= 100, "Name must have 2-100 characters in length!");
        this.name = name;
        return this;
    }

    // Channels

    @Nonnull
    @Override
    @CheckReturnValue
    public ChannelData getChannel(int index) {
        return this.channels.get(index);
    }

    // Roles

    @Override
    protected RequestBody finalizeData() {
        final DataObject object = DataObject.empty();
        object.put("name", name);
        object.put("roles", DataArray.fromCollection(roles));
        if (!channels.isEmpty())
            object.put("channels", DataArray.fromCollection(channels));
        if (icon != null)
            object.put("icon", icon.getEncoding());
        if (verificationLevel != null)
            object.put("verification_level", verificationLevel.getKey());
        if (notificationLevel != null)
            object.put("default_message_notifications", notificationLevel.getKey());
        if (explicitContentLevel != null)
            object.put("explicit_content_filter", explicitContentLevel.getKey());
        if (region != null)
            object.put("region", region.getKey());
        return getRequestBody(object);
    }
}
