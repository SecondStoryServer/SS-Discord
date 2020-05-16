package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;


@SuppressWarnings("ALL")
public interface Member extends IMentionable, IPermissionHolder, IFakeable {

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


    @Nonnull
    List<Activity> getActivities();


    @Nonnull
    OnlineStatus getOnlineStatus();


    @Nullable
    String getNickname();


    @Nonnull
    String getEffectiveName();


    @Nonnull
    List<Role> getRoles();


    boolean canInteract(@Nonnull Member member);


    boolean canInteract(@Nonnull Role role);


    boolean isOwner();


}
