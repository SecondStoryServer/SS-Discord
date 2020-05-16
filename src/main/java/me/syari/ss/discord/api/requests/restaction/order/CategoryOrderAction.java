package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;


public interface CategoryOrderAction extends ChannelOrderAction {

    @Nonnull
    Category getCategory();
}
