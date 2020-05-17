package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;

public class TextChannelImpl extends AbstractChannelImpl<TextChannel, TextChannelImpl> implements TextChannel {

    public TextChannelImpl(long id, GuildImpl guild) {
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
    public ChannelType getType() {
        return ChannelType.TEXT;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return api;
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull CharSequence text) {
        return TextChannel.super.sendMessage(text);
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull Message msg) {
        Checks.notNull(msg, "Message");
        return TextChannel.super.sendMessage(msg);
    }

    @Override
    public String toString() {
        return "TC:" + getName() + '(' + id + ')';
    }
}
