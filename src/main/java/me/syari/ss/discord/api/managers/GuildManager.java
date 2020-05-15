

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Icon;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface GuildManager extends Manager<GuildManager>
{

    long NAME   = 0x1;

    long REGION = 0x2;

    long ICON   = 0x4;

    long SPLASH = 0x8;

    long AFK_CHANNEL    = 0x10;

    long AFK_TIMEOUT    = 0x20;

    long SYSTEM_CHANNEL = 0x40;

    long MFA_LEVEL      = 0x80;

    long NOTIFICATION_LEVEL     = 0x100;

    long EXPLICIT_CONTENT_LEVEL = 0x200;

    long VERIFICATION_LEVEL     = 0x400;

    long BANNER                 = 0x800;

    long VANITY_URL   = 0x1000;

    long DESCRIPTION  = 0x2000;


    @Nonnull
    @Override
    GuildManager reset(long fields);


    @Nonnull
    @Override
    GuildManager reset(long... fields);


    @Nonnull
    Guild getGuild();


    @Nonnull
    @CheckReturnValue
    GuildManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    GuildManager setRegion(@Nonnull Region region);


    @Nonnull
    @CheckReturnValue
    GuildManager setIcon(@Nullable Icon icon);


    @Nonnull
    @CheckReturnValue
    GuildManager setSplash(@Nullable Icon splash);


    @Nonnull
    @CheckReturnValue
    GuildManager setAfkChannel(@Nullable VoiceChannel afkChannel);


    @Nonnull
    @CheckReturnValue
    GuildManager setSystemChannel(@Nullable TextChannel systemChannel);


    @Nonnull
    @CheckReturnValue
    GuildManager setAfkTimeout(@Nonnull Guild.Timeout timeout);


    @Nonnull
    @CheckReturnValue
    GuildManager setVerificationLevel(@Nonnull Guild.VerificationLevel level);


    @Nonnull
    @CheckReturnValue
    GuildManager setDefaultNotificationLevel(@Nonnull Guild.NotificationLevel level);


    @Nonnull
    @CheckReturnValue
    GuildManager setRequiredMFALevel(@Nonnull Guild.MFALevel level);


    @Nonnull
    @CheckReturnValue
    GuildManager setExplicitContentLevel(@Nonnull Guild.ExplicitContentLevel level);


    @Nonnull
    @CheckReturnValue
    GuildManager setBanner(@Nullable Icon banner);


    @Nonnull
    @CheckReturnValue
    GuildManager setVanityCode(@Nullable String code);


    @Nonnull
    @CheckReturnValue
    GuildManager setDescription(@Nullable String description);
}
