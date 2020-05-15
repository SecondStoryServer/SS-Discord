

package me.syari.ss.discord.api.audio.hooks;

import me.syari.ss.discord.api.audio.SpeakingMode;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import java.util.EnumSet;


public interface ConnectionListener
{

    void onPing(long ping);


    void onStatusChange(@Nonnull ConnectionStatus status);


    void onUserSpeaking(@Nonnull User user, boolean speaking);


    default void onUserSpeaking(@Nonnull User user, @Nonnull EnumSet<SpeakingMode> modes) {}



    default void onUserSpeaking(@Nonnull User user, boolean speaking, boolean soundshare) {}
}
