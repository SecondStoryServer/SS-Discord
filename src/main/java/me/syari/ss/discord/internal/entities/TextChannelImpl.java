package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.exceptions.VerificationLevelException;
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
    public MessageAction sendMessage(@Nonnull CharSequence text) {
        checkVerification();
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        return TextChannel.super.sendMessage(text);
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull MessageEmbed embed) {
        checkVerification();
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        // this is checked because you cannot send an empty message
        checkPermission(Permission.MESSAGE_EMBED_LINKS);
        return TextChannel.super.sendMessage(embed);
    }

    @Nonnull
    @Override
    public MessageAction sendMessage(@Nonnull Message msg) {
        Checks.notNull(msg, "Message");

        checkVerification();
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        if (msg.getContentRaw().isEmpty() && !msg.getEmbeds().isEmpty())
            checkPermission(Permission.MESSAGE_EMBED_LINKS);

        //Call MessageChannel's default
        return TextChannel.super.sendMessage(msg);
    }

    @Override
    public String toString() {
        return "TC:" + getName() + '(' + id + ')';
    }

    private void checkVerification() {
        if (!getGuild().checkVerification())
            throw new VerificationLevelException(getGuild().getVerificationLevel());
    }
}
