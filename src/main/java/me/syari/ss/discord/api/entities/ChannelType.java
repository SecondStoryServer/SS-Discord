package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;

public enum ChannelType {

    TEXT(0, 0, true),

    UNKNOWN(-1, -2);

    private final int sortBucket;
    private final int id;
    private final boolean isGuild;

    ChannelType(int id, int sortBucket) {
        this(id, sortBucket, false);
    }

    ChannelType(int id, int sortBucket, boolean isGuild) {
        this.id = id;
        this.sortBucket = sortBucket;
        this.isGuild = isGuild;
    }


    public int getSortBucket() {
        return sortBucket;
    }


    public boolean isGuild() {
        return isGuild;
    }


    @Nonnull
    public static ChannelType fromId(int id) {
        if(id == TEXT.id){
            return TEXT;
        } else {
            return UNKNOWN;
        }
    }


}
