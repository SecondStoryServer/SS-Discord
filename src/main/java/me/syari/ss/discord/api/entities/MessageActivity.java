
package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public class MessageActivity
{

    public MessageActivity()
    {
    }


    public static class Application implements ISnowflake
    {
        private final long id;

        public Application(long id)
        {
            this.id = id;
        }


        @Override
        public long getIdLong()
        {
            return id;
        }
    }


    public enum ActivityType
    {

        JOIN(1),

        SPECTATE(2),

        LISTENING(3),

        JOIN_REQUEST(5),

        UNKNOWN(-1);

        private final int id;

        ActivityType(int id)
        {
            this.id = id;
        }


        @Nonnull
        public static ActivityType fromId(int id)
        {
            for (ActivityType activityType : values())
            {
                if (activityType.id == id)
                    return activityType;
            }
            return UNKNOWN;
        }
    }
}
