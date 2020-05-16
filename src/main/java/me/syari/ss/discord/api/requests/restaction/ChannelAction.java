package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BooleanSupplier;


public interface ChannelAction<T extends GuildChannel> extends AuditableRestAction<T> {
    @Nonnull
    @Override
    ChannelAction<T> setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    Guild getGuild();


    @Nonnull
    ChannelType getType();


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setParent(@Nullable Category category);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setPosition(@Nullable Integer position);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setTopic(@Nullable String topic);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setNSFW(boolean nsfw);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setSlowmode(int slowmode);


    @Nonnull
    @CheckReturnValue
    default ChannelAction<T> addPermissionOverride(@Nonnull IPermissionHolder target, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny) {
        final long allowRaw = allow != null ? Permission.getRaw(allow) : 0;
        final long denyRaw = deny != null ? Permission.getRaw(deny) : 0;

        return addPermissionOverride(target, allowRaw, denyRaw);
    }


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> addPermissionOverride(@Nonnull IPermissionHolder target, long allow, long deny);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setBitrate(@Nullable Integer bitrate);


    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setUserlimit(@Nullable Integer userlimit);
}
