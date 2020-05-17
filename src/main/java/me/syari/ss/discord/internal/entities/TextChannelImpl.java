package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;

public class TextChannelImpl extends AbstractChannelImpl<TextChannel, TextChannelImpl> implements TextChannel {

    public TextChannelImpl(long id, Guild guild) {
        super(id, guild);
    }

    @Override
    public TextChannelImpl setPosition(int rawPosition) {
        getGuild().getTextChannelsView().clearCachedLists();
        return super.setPosition(rawPosition);
    }

    @Nonnull
    @Override
    public String getAsMention() {
        return "<#" + id + '>';
    }


    @Nonnull
    @Override
    public JDA getJDA() {
        return api;
    }

    @Override
    public String toString() {
        return "TC:" + getName() + '(' + id + ')';
    }
}
