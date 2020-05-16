package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.Incubating;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public interface Activity {


    @Nonnull
    String getName();


    @Nullable
    String getUrl();


    @Nonnull
    ActivityType getType();


    enum ActivityType {

        DEFAULT(0),

        STREAMING(1),

        LISTENING(2),

        @Incubating
        WATCHING(3),

        @Incubating
        CUSTOM_STATUS(4);

        private final int key;

        ActivityType(int key) {
            this.key = key;
        }


        public int getKey() {
            return key;
        }


        @Nonnull
        public static ActivityType fromKey(int key) {
            switch (key) {
                case 0:
                default:
                    return DEFAULT;
                case 1:
                    return STREAMING;
                case 2:
                    return LISTENING;
                case 3:
                    return WATCHING;
                case 4:
                    return CUSTOM_STATUS;
            }
        }
    }


    class Timestamps {
        protected final long start;

        protected final long end;

        public Timestamps(long start, long end) {
            this.start = start;
            this.end = end;
        }


        @Override
        public String toString() {
            return String.format("RichPresenceTimestamp(%d-%d)", start, end);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Timestamps))
                return false;
            Timestamps t = (Timestamps) obj;
            return start == t.start && end == t.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }


}
