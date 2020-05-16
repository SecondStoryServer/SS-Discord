package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;
import java.util.List;


public interface Category extends GuildChannel {

    @Nonnull
    List<GuildChannel> getChannels();


    @Nonnull
    List<TextChannel> getTextChannels();

}
