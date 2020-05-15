

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public interface DirectAudioController
{

    @Nonnull
    JDA getJDA();


    void connect(@Nonnull VoiceChannel channel);


    void disconnect(@Nonnull Guild guild);


    void reconnect(@Nonnull VoiceChannel channel);
}
