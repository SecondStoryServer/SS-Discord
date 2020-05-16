

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import javax.annotation.Nonnull;
import java.util.List;


public interface Category extends GuildChannel
{
    
    @Nonnull
    List<GuildChannel> getChannels();

    
    @Nonnull
    List<StoreChannel> getStoreChannels();

    
    @Nonnull
    List<TextChannel> getTextChannels();

    
    @Nonnull
    List<VoiceChannel> getVoiceChannels();


    @Nonnull
    @Override
    ChannelAction<Category> createCopy(@Nonnull Guild guild);

    @Nonnull
    @Override
    ChannelAction<Category> createCopy();
}
