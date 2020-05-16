package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class VoiceChannelUpdateParentEvent extends GenericVoiceChannelUpdateEvent<Category> {
    public static final String IDENTIFIER = "parent";

    public VoiceChannelUpdateParentEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, @Nullable Category oldParent) {
        super(api, responseNumber, channel, oldParent, channel.getParent(), IDENTIFIER);
    }


    @Nullable
    public Category getOldParent() {
        return getOldValue();
    }


    @Nullable
    public Category getNewParent() {
        return getNewValue();
    }
}
