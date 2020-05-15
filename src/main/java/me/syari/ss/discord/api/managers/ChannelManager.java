

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;


public interface ChannelManager extends Manager<ChannelManager>
{

    long NAME       = 0x1;

    long PARENT     = 0x2;

    long TOPIC      = 0x4;

    long POSITION   = 0x8;

    long NSFW       = 0x10;

    long USERLIMIT  = 0x20;

    long BITRATE    = 0x40;

    long PERMISSION = 0x80;

    long SLOWMODE   = 0x100;


    @Nonnull
    @Override
    ChannelManager reset(long fields);


    @Nonnull
    @Override
    ChannelManager reset(long... fields);


    @Nonnull
    GuildChannel getChannel();


    @Nonnull
    default ChannelType getType()
    {
        return getChannel().getType();
    }


    @Nonnull
    default Guild getGuild()
    {
        return getChannel().getGuild();
    }


    @Nonnull
    @CheckReturnValue
    ChannelManager clearOverridesAdded();


    @Nonnull
    @CheckReturnValue
    ChannelManager clearOverridesRemoved();


    @Nonnull
    @CheckReturnValue
    ChannelManager putPermissionOverride(@Nonnull IPermissionHolder permHolder, long allow, long deny);


    @Nonnull
    @CheckReturnValue
    default ChannelManager putPermissionOverride(@Nonnull IPermissionHolder permHolder, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
    {
        long allowRaw = allow == null ? 0 : Permission.getRaw(allow);
        long denyRaw  = deny  == null ? 0 : Permission.getRaw(deny);
        return putPermissionOverride(permHolder, allowRaw, denyRaw);
    }


    @Nonnull
    @CheckReturnValue
    ChannelManager removePermissionOverride(@Nonnull IPermissionHolder permHolder);


    @Nonnull
    @CheckReturnValue
    default ChannelManager sync()
    {
        if (getChannel().getParent() == null)
            throw new IllegalStateException("sync() requires a parent category");
        return sync(getChannel().getParent());
    }


    @Nonnull
    @CheckReturnValue
    ChannelManager sync(@Nonnull GuildChannel syncSource);


    @Nonnull
    @CheckReturnValue
    ChannelManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    ChannelManager setParent(@Nullable Category category);


    @Nonnull
    @CheckReturnValue
    ChannelManager setPosition(int position);


    @Nonnull
    @CheckReturnValue
    ChannelManager setTopic(@Nullable String topic);


    @Nonnull
    @CheckReturnValue
    ChannelManager setNSFW(boolean nsfw);


    @Nonnull
    @CheckReturnValue
    ChannelManager setSlowmode(int slowmode);


    @Nonnull
    @CheckReturnValue
    ChannelManager setUserLimit(int userLimit);


    @Nonnull
    @CheckReturnValue
    ChannelManager setBitrate(int bitrate);
}
