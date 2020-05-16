package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

public interface Member extends IMentionable, IFakeable {

    @Nonnull
    User getUser();


    @Nonnull
    Guild getGuild();


    @Nonnull
    JDA getJDA();


    @Nonnull
    OffsetDateTime getTimeJoined();


    @Nullable
    String getNickname();


    @Nonnull
    String getDisplayName();


    boolean isOwner();
}
