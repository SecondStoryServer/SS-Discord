package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;


public class TextChannelUpdateSlowmodeEvent extends GenericTextChannelUpdateEvent<Integer> {
    public static final String IDENTIFIER = "slowmode";

    public TextChannelUpdateSlowmodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, int oldSlowmode) {
        super(api, responseNumber, channel, oldSlowmode, channel.getSlowmode(), IDENTIFIER);
    }


    public int getOldSlowmode() {
        return getOldValue();
    }


    public int getNewSlowmode() {
        return getNewValue();
    }
}
