package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.Incubating;
import me.syari.ss.discord.internal.utils.EncodingUtil;

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


    class Emoji implements ISnowflake, IMentionable {
        private final String name;
        private final long id;
        private final boolean animated;

        public Emoji(String name, long id, boolean animated) {
            this.name = name;
            this.id = id;
            this.animated = animated;
        }


        @Nonnull
        public String getAsCodepoints() {
            if (!isEmoji())
                throw new IllegalStateException("Cannot convert custom emote to codepoints");
            return EncodingUtil.encodeCodepoints(name);
        }


        @Override
        public long getIdLong() {
            if (!isEmote())
                throw new IllegalStateException("Cannot get id for unicode emoji");
            return id;
        }


        public boolean isAnimated() {
            return animated;
        }


        public boolean isEmoji() {
            return id == 0;
        }


        public boolean isEmote() {
            return id != 0;
        }

        @Nonnull
        @Override
        public String getAsMention() {
            if (isEmoji())
                return name; // unicode name
            // custom emoji format (for messages)
            return String.format("<%s:%s:%s>", isAnimated() ? "a" : "", name, getId());
        }

        @Override
        public int hashCode() {
            return id == 0 ? name.hashCode() : Long.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof Emoji))
                return false;
            Emoji other = (Emoji) obj;
            return id == 0 ? other.name.equals(this.name)
                    : other.id == this.id;
        }

        @Override
        public String toString() {
            if (isEmoji())
                return "ActivityEmoji(" + getAsCodepoints() + ')';
            return "ActivityEmoji(" + Long.toUnsignedString(id) + " / " + name + ')';
        }
    }
}
