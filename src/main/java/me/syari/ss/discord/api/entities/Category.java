

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import me.syari.ss.discord.api.requests.restaction.order.CategoryOrderAction;
import me.syari.ss.discord.api.requests.restaction.order.ChannelOrderAction;
import me.syari.ss.discord.api.requests.restaction.order.OrderAction;

import javax.annotation.CheckReturnValue;
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
    @CheckReturnValue
    ChannelAction<TextChannel> createTextChannel(@Nonnull String name);

    
    @Nonnull
    @CheckReturnValue
    ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name);

    
    @Nonnull
    @CheckReturnValue
    CategoryOrderAction modifyTextChannelPositions();

    
    @Nonnull
    @CheckReturnValue
    CategoryOrderAction modifyVoiceChannelPositions();

    @Nonnull
    @Override
    ChannelAction<Category> createCopy(@Nonnull Guild guild);

    @Nonnull
    @Override
    ChannelAction<Category> createCopy();
}
