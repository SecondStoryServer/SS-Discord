
package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MessageActivity
{
    private final ActivityType type;
    private final String partyId;
    private final Application application;

    public MessageActivity(ActivityType type, String partyId, Application application)
    {
        this.type = type;
        this.partyId = partyId;
        this.application = application;
    }


    @Nonnull
    public ActivityType getType()
    {
        return type;
    }


    @Nullable
    public String getPartyId()
    {
        return partyId;
    }


    @Nullable
    public MessageActivity.Application getApplication()
    {
        return application;
    }


    public static class Application implements ISnowflake
    {
        private final String name;
        private final String description;
        private final String iconId;
        private final String coverId;
        private final long id;

        public Application(String name, String description, String iconId, String coverId, long id)
        {
            this.name = name;
            this.description = description;
            this.iconId = iconId;
            this.coverId = coverId;
            this.id = id;
        }


        @Nonnull
        public String getName()
        {
            return name;
        }


        @Nonnull
        public String getDescription()
        {
            return description;
        }


        @Nullable
        public String getIconId()
        {
            return iconId;
        }


        @Nullable
        public String getIconUrl()
        {
            return iconId == null ? null : "https://cdn.discordapp.com/application/" + getId() + "/" + iconId + ".png";
        }


        @Nullable
        public String getCoverId()
        {
            return coverId;
        }


        @Nullable
        public String getCoverUrl()
        {
            return coverId == null ? null : "https://cdn.discordapp.com/application/" + getId() + "/" + coverId + ".png";
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


        public int getId()
        {
            return id;
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
