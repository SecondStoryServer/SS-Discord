package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nullable;


public interface ShardCacheView extends CacheView<JDA> {

    @Nullable
    JDA getElementById(int id);


}
