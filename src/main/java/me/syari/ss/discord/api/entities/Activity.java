package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.Incubating;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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


    }


}
