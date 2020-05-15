

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;


public interface Member extends IMentionable, IPermissionHolder, IFakeable
{

    @Nonnull
    User getUser();


    @Nonnull
    Guild getGuild();


    @Nonnull
    JDA getJDA();


    @Nonnull
    OffsetDateTime getTimeJoined();


    @Nullable
    OffsetDateTime getTimeBoosted();


    @Nullable
    GuildVoiceState getVoiceState();


    @Nonnull
    List<Activity> getActivities();


    @Nonnull
    OnlineStatus getOnlineStatus();


    @Nonnull
    OnlineStatus getOnlineStatus(@Nonnull ClientType type);


    @Nonnull
    EnumSet<ClientType> getActiveClients();


    @Nullable
    String getNickname();


    @Nonnull
    String getEffectiveName();


    @Nonnull
    List<Role> getRoles();


    @Nullable
    Color getColor();


    int getColorRaw();


    boolean canInteract(@Nonnull Member member);


    boolean canInteract(@Nonnull Role role);


    boolean canInteract(@Nonnull Emote emote);


    boolean isOwner();


    @Nullable
    TextChannel getDefaultChannel();


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(int delDays)
    {
        return getGuild().ban(this, delDays);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(int delDays, @Nullable String reason)
    {
        return getGuild().ban(this, delDays, reason);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> kick()
    {
        return getGuild().kick(this);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> kick(@Nullable String reason)
    {
        return getGuild().kick(this, reason);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> mute(boolean mute)
    {
        return getGuild().mute(this, mute);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> deafen(boolean deafen)
    {
        return getGuild().deafen(this, deafen);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> modifyNickname(@Nullable String nickname)
    {
        return getGuild().modifyNickname(this, nickname);
    }
}
