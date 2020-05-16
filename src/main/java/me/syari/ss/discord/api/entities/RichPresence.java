package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;


public interface RichPresence extends Activity {

    long getApplicationIdLong();


    @Nonnull
    String getApplicationId();


    @Nullable
    String getSessionId();


    @Nullable
    String getSyncId();


    int getFlags();


    EnumSet<ActivityFlag> getFlagSet();


    @Nullable
    String getState();


    @Nullable
    String getDetails();


    @Nullable
    Party getParty();


    @Nullable
    Image getLargeImage();


    @Nullable
    Image getSmallImage();


    class Image {
        protected final String key;
        protected final String text;
        protected final String applicationId;

        public Image(long applicationId, String key, String text) {
            this.applicationId = Long.toUnsignedString(applicationId);
            this.key = key;
            this.text = text;
        }


        @Nonnull
        public String getKey() {
            return key;
        }


        @Nullable
        public String getText() {
            return text;
        }


        @Nonnull
        public String getUrl() {
            if (key.startsWith("spotify:"))
                return "https://i.scdn.co/image/" + key.substring("spotify:".length());
            if (key.startsWith("twitch:"))
                return String.format("https://static-cdn.jtvnw.net/previews-ttv/live_user_%s-1920x1080.png", key.substring("twitch:".length()));
            return "https://cdn.discordapp.com/app-assets/" + applicationId + "/" + key + ".png";
        }

        @Override
        public String toString() {
            return String.format("RichPresenceImage(%s | %s)", key, text);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Image))
                return false;
            Image i = (Image) obj;
            return Objects.equals(key, i.key) && Objects.equals(text, i.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, text);
        }
    }


    class Party {
        protected final String id;
        protected final long size;
        protected final long max;

        public Party(String id, long size, long max) {
            this.id = id;
            this.size = size;
            this.max = max;
        }


        @Nullable
        public String getId() {
            return id;
        }


        public long getSize() {
            return size;
        }


        public long getMax() {
            return max;
        }

        @Override
        public String toString() {
            return String.format("RichPresenceParty(%s | [%d, %d])", id, size, max);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Party))
                return false;
            Party p = (Party) obj;
            return size == p.size && max == p.max && Objects.equals(id, p.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, size, max);
        }
    }
}
