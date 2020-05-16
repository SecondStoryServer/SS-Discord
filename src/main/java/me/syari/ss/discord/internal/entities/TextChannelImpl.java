package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.VerificationLevelException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.utils.AttachmentOption;
import me.syari.ss.discord.api.utils.TimeUtil;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TextChannelImpl extends AbstractChannelImpl<TextChannel, TextChannelImpl> implements TextChannel {
    private String topic;
    private long lastMessageId;
    private boolean nsfw;
    private int slowmode;

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
    public List<CompletableFuture<Void>> purgeMessages(@Nonnull List<? extends Message> messages) {
        if (messages.isEmpty())
            return Collections.emptyList();
        boolean hasPerms = getGuild().getSelfMember().hasPermission(this, Permission.MESSAGE_MANAGE);
        if (!hasPerms) {
            for (Message m : messages) {
                if (m.getAuthor().equals(getJDA().getSelfUser()))
                    continue;
                throw new InsufficientPermissionException(this, Permission.MESSAGE_MANAGE, "Cannot delete messages of other users");
            }
        }
        return TextChannel.super.purgeMessages(messages);
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public List<CompletableFuture<Void>> purgeMessagesById(@Nonnull long... messageIds) {
        if (messageIds == null || messageIds.length == 0)
            return Collections.emptyList();
        if (!getGuild().getSelfMember().hasPermission(this, Permission.MESSAGE_MANAGE))
            return TextChannel.super.purgeMessagesById(messageIds);

        // remove duplicates and sort messages
        List<CompletableFuture<Void>> list = new LinkedList<>();
        TreeSet<Long> bulk = new TreeSet<>(Comparator.reverseOrder());
        TreeSet<Long> norm = new TreeSet<>(Comparator.reverseOrder());
        long twoWeeksAgo = TimeUtil.getDiscordTimestamp(System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000) + 10000);
        for (long messageId : messageIds) {
            if (messageId > twoWeeksAgo)
                bulk.add(messageId);
            else
                norm.add(messageId);
        }

        // delete chunks of 100 messages each
        if (!bulk.isEmpty()) {
            List<String> toDelete = new ArrayList<>(100);
            while (!bulk.isEmpty()) {
                toDelete.clear();
                for (int i = 0; i < 100 && !bulk.isEmpty(); i++)
                    toDelete.add(Long.toUnsignedString(bulk.pollLast()));
                if (toDelete.size() == 1)
                    list.add(deleteMessageById(toDelete.get(0)).submit());
                else if (!toDelete.isEmpty())
                    list.add(deleteMessages0(toDelete).submit());
            }
        }

        // delete messages too old for bulk delete
        if (!norm.isEmpty()) {
            for (long message : norm)
                list.add(deleteMessageById(message).submit());
        }
        return list;
    }

    @Nonnull
    @Override
    public ChannelType getType() {
        return ChannelType.TEXT;
    }

    @Nonnull
    @Override
    public List<Member> getMembers() {
        return Collections.unmodifiableList(getGuild().getMembersView().stream()
                .filter(m -> m.hasPermission(this, Permission.MESSAGE_READ))
                .collect(Collectors.toList()));
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

    @Nonnull
    @Override
    public MessageAction sendFile(@Nonnull InputStream data, @Nonnull String fileName, @Nonnull AttachmentOption... options) {
        checkVerification();
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        checkPermission(Permission.MESSAGE_ATTACH_FILES);

        //Call MessageChannel's default method
        return TextChannel.super.sendFile(data, fileName, options);
    }

    @Nonnull
    @Override
    public RestAction<Message> retrieveMessageById(@Nonnull String messageId) {
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_HISTORY);

        //Call MessageChannel's default method
        return TextChannel.super.retrieveMessageById(messageId);
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> deleteMessageById(@Nonnull String messageId) {
        Checks.isSnowflake(messageId, "Message ID");
        checkPermission(Permission.MESSAGE_READ);

        //Call MessageChannel's default method
        return TextChannel.super.deleteMessageById(messageId);
    }

    @Nonnull
    @Override
    public RestAction<Void> pinMessageById(@Nonnull String messageId) {
        checkPermission(Permission.MESSAGE_READ, "You cannot pin a message in a channel you can't access. (MESSAGE_READ)");
        checkPermission(Permission.MESSAGE_MANAGE, "You need MESSAGE_MANAGE to pin or unpin messages.");

        //Call MessageChannel's default method
        return TextChannel.super.pinMessageById(messageId);
    }

    @Nonnull
    @Override
    public RestAction<Void> unpinMessageById(@Nonnull String messageId) {
        checkPermission(Permission.MESSAGE_READ, "You cannot unpin a message in a channel you can't access. (MESSAGE_READ)");
        checkPermission(Permission.MESSAGE_MANAGE, "You need MESSAGE_MANAGE to pin or unpin messages.");

        //Call MessageChannel's default method
        return TextChannel.super.unpinMessageById(messageId);
    }

    @Nonnull
    @Override
    public RestAction<List<Message>> retrievePinnedMessages() {
        checkPermission(Permission.MESSAGE_READ, "Cannot get the pinned message of a channel without MESSAGE_READ access.");

        //Call MessageChannel's default method
        return TextChannel.super.retrievePinnedMessages();
    }

    @Nonnull
    @Override
    public RestAction<Void> addReactionById(@Nonnull String messageId, @Nonnull String unicode) {
        checkPermission(Permission.MESSAGE_HISTORY);

        //Call MessageChannel's default method
        return TextChannel.super.addReactionById(messageId, unicode);
    }

    @Nonnull
    @Override
    public RestAction<Void> addReactionById(@Nonnull String messageId, @Nonnull Emote emote) {
        checkPermission(Permission.MESSAGE_HISTORY);

        //Call MessageChannel's default method
        return TextChannel.super.addReactionById(messageId, emote);
    }

    @Nonnull
    @Override
    public MessageAction editMessageById(@Nonnull String messageId, @Nonnull CharSequence newContent) {
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        return TextChannel.super.editMessageById(messageId, newContent);
    }

    @Nonnull
    @Override
    public MessageAction editMessageById(@Nonnull String messageId, @Nonnull MessageEmbed newEmbed) {
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        checkPermission(Permission.MESSAGE_EMBED_LINKS);
        return TextChannel.super.editMessageById(messageId, newEmbed);
    }

    @Nonnull
    @Override
    public MessageAction editMessageById(@Nonnull String id, @Nonnull Message newContent) {
        Checks.notNull(newContent, "Message");

        //checkVerification(); no verification needed to edit a message
        checkPermission(Permission.MESSAGE_READ);
        checkPermission(Permission.MESSAGE_WRITE);
        if (newContent.getContentRaw().isEmpty() && !newContent.getEmbeds().isEmpty())
            checkPermission(Permission.MESSAGE_EMBED_LINKS);

        //Call MessageChannel's default
        return TextChannel.super.editMessageById(id, newContent);
    }

    @Override
    public String toString() {
        return "TC:" + getName() + '(' + id + ')';
    }

    // -- Setters --

    public TextChannelImpl setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public TextChannelImpl setLastMessageId(long id) {
        this.lastMessageId = id;
        return this;
    }

    public TextChannelImpl setNSFW(boolean nsfw) {
        this.nsfw = nsfw;
        return this;
    }

    public TextChannelImpl setSlowmode(int slowmode) {
        this.slowmode = slowmode;
        return this;
    }

    // -- internal --
    private RestActionImpl<Void> deleteMessages0(Collection<String> messageIds) {
        DataObject body = DataObject.empty().put("messages", messageIds);
        Route.CompiledRoute route = Route.Messages.DELETE_MESSAGES.compile(getId());
        return new RestActionImpl<>(getJDA(), route, body);
    }

    private void checkVerification() {
        if (!getGuild().checkVerification())
            throw new VerificationLevelException(getGuild().getVerificationLevel());
    }
}
