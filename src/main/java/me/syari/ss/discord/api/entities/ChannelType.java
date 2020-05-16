package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;
import java.util.EnumSet;


public enum ChannelType {

    TEXT(0, 0, true),

    PRIVATE(1, -1),

    VOICE(2, 1, true),

    GROUP(3, -1),

    CATEGORY(4, 2, true),

    STORE(6, 0, true),

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


    public int getId() {
        return id;
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


    @Nonnull
    public static EnumSet<ChannelType> fromSortBucket(int bucket) {
        EnumSet<ChannelType> types = EnumSet.noneOf(ChannelType.class);
        for (ChannelType type : values()) {
            if (type.getSortBucket() == bucket)
                types.add(type);
        }
        return types;
    }
}
