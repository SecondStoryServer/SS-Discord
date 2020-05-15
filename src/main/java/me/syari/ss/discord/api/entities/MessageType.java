
package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public enum MessageType
{

    DEFAULT(0),


    RECIPIENT_ADD(1),


    RECIPIENT_REMOVE(2),


    CALL(3),


    CHANNEL_NAME_CHANGE(4),


    CHANNEL_ICON_CHANGE(5),


    CHANNEL_PINNED_ADD(6),


    GUILD_MEMBER_JOIN(7),


    GUILD_MEMBER_BOOST(8),


    GUILD_BOOST_TIER_1(9),


    GUILD_BOOST_TIER_2(10),


    GUILD_BOOST_TIER_3(11),


    CHANNEL_FOLLOW_ADD(12),


    UNKNOWN(-1);

    protected final int id;

    MessageType(int id)
    {
        this.id = id;
    }


    public int getId()
    {
        return id;
    }


    @Nonnull
    public static MessageType fromId(int id)
    {
        for (MessageType type : values())
        {
            if (type.id == id)
                return type;
        }
        return UNKNOWN;
    }
}
