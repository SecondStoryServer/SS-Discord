package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;

public enum ChannelType {

    TEXT(0, 0, true),

    PRIVATE(1, -1),

    GROUP(3, -1),

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
        if (id == 5) // NEWS = TEXT
            return TEXT;
        for (ChannelType type : values()) {
            if (type.id == id)
                return type;
        }
        return UNKNOWN;
    }


}
