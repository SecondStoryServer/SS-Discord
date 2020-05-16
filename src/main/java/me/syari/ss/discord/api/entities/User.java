package me.syari.ss.discord.api.entities;


import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;


public interface User extends IMentionable, IFakeable {

    Pattern USER_TAG = Pattern.compile("(.{2,32})#(\\d{4})");


    String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s.%s";

    String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/%s.png";


    @Nonnull
    String getName();


    @Nonnull
    String getDiscriminator();


    @Nullable
    String getAvatarId();


    @Nullable
    default String getAvatarUrl() {
        String avatarId = getAvatarId();
        return avatarId == null ? null : String.format(AVATAR_URL, getId(), avatarId, avatarId.startsWith("a_") ? "gif" : "png");
    }


    @Nonnull
    String getDefaultAvatarId();


    @Nonnull
    default String getDefaultAvatarUrl() {
        return String.format(DEFAULT_AVATAR_URL, getDefaultAvatarId());
    }


    @Nonnull
    default String getEffectiveAvatarUrl() {
        String avatarUrl = getAvatarUrl();
        return avatarUrl == null ? getDefaultAvatarUrl() : avatarUrl;
    }


    @Nonnull
    String getAsTag();


    boolean hasPrivateChannel();


    @Nonnull
    @CheckReturnValue
    RestAction<PrivateChannel> openPrivateChannel();


    @Nonnull
    List<Guild> getMutualGuilds();


    boolean isBot();


    @Nonnull
    JDA getJDA();
}
