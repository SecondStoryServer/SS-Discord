package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.pagination.ReactionPaginationAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.pagination.ReactionPaginationActionImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.EncodingUtil;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public class MessageReaction {
    private final MessageChannel channel;
    private final ReactionEmote emote;
    private final long messageId;
    private final boolean self;
    private final int count;


    public MessageReaction(@Nonnull MessageChannel channel, @Nonnull ReactionEmote emote, long messageId, boolean self, int count) {
        this.channel = channel;
        this.emote = emote;
        this.messageId = messageId;
        this.self = self;
        this.count = count;
    }


    @Nonnull
    public JDA getJDA() {
        return channel.getJDA();
    }


    public boolean isSelf() {
        return self;
    }


    public boolean hasCount() {
        return count >= 0;
    }


    public int getCount() {
        if (!hasCount())
            throw new IllegalStateException("Cannot retrieve count for this MessageReaction!");
        return count;
    }


    @Nonnull
    public ChannelType getChannelType() {
        return channel.getType();
    }


    public boolean isFromType(@Nonnull ChannelType type) {
        return getChannelType() == type;
    }


    @Nullable
    public Guild getGuild() {
        TextChannel channel = getTextChannel();
        return channel != null ? channel.getGuild() : null;
    }


    @Nullable
    public TextChannel getTextChannel() {
        return getChannel() instanceof TextChannel ? (TextChannel) getChannel() : null;
    }


    @Nullable
    public PrivateChannel getPrivateChannel() {
        return getChannel() instanceof PrivateChannel ? (PrivateChannel) getChannel() : null;
    }


    @Nonnull
    public MessageChannel getChannel() {
        return channel;
    }


    @Nonnull
    public ReactionEmote getReactionEmote() {
        return emote;
    }


    @Nonnull
    public String getMessageId() {
        return Long.toUnsignedString(messageId);
    }


    public long getMessageIdLong() {
        return messageId;
    }


    @Nonnull
    @CheckReturnValue
    public ReactionPaginationAction retrieveUsers() {
        return new ReactionPaginationActionImpl(this);
    }


    @Nonnull
    @CheckReturnValue
    public RestAction<Void> removeReaction() {
        return removeReaction(getJDA().getSelfUser());
    }


    @Nonnull
    @CheckReturnValue
    public RestAction<Void> removeReaction(@Nonnull User user) {
        Checks.notNull(user, "User");
        boolean self = user.equals(getJDA().getSelfUser());
        if (!self) {
            if (channel.getType() == ChannelType.TEXT) {
                GuildChannel channel = (GuildChannel) this.channel;
                if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
                    throw new InsufficientPermissionException(channel, Permission.MESSAGE_MANAGE);
            } else {
                throw new PermissionException("Unable to remove Reaction of other user in non-text channel!");
            }
        }

        String code = emote.isEmote()
                ? emote.getName() + ":" + emote.getId()
                : EncodingUtil.encodeUTF8(emote.getName());
        String target = self ? "@me" : user.getId();
        Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(channel.getId(), getMessageId(), code, target);
        return new RestActionImpl<>(getJDA(), route);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof MessageReaction))
            return false;
        MessageReaction r = (MessageReaction) obj;
        return r.emote.equals(emote)
                && r.self == self
                && r.messageId == messageId;
    }

    @Override
    public String toString() {
        return "MR:(M:(" + messageId + ") / " + emote + ")";
    }


    public static class ReactionEmote implements ISnowflake {
        private final JDA api;
        private final String name;
        private final long id;
        private final Emote emote;

        private ReactionEmote(@Nonnull String name, @Nonnull JDA api) {
            this.name = name;
            this.api = api;
            this.id = 0;
            this.emote = null;
        }

        private ReactionEmote(@Nonnull Emote emote) {
            this.api = emote.getJDA();
            this.name = emote.getName();
            this.id = emote.getIdLong();
            this.emote = emote;
        }

        @Nonnull
        public static ReactionEmote fromUnicode(@Nonnull String name, @Nonnull JDA api) {
            return new ReactionEmote(name, api);
        }

        @Nonnull
        public static ReactionEmote fromCustom(@Nonnull Emote emote) {
            return new ReactionEmote(emote);
        }


        public boolean isEmote() {
            return emote != null;
        }


        public boolean isEmoji() {
            return emote == null;
        }


        @Nonnull
        public String getName() {
            return name;
        }


        @Nonnull
        public String getAsCodepoints() {
            if (!isEmoji())
                throw new IllegalStateException("Cannot get codepoint for custom emote reaction");
            return EncodingUtil.encodeCodepoints(name);
        }

        @Override
        public long getIdLong() {
            if (!isEmote())
                throw new IllegalStateException("Cannot get id for emoji reaction");
            return id;
        }


        @Nonnull
        public String getEmoji() {
            if (!isEmoji())
                throw new IllegalStateException("Cannot get emoji code for custom emote reaction");
            return getName();
        }


        @Nonnull
        public Emote getEmote() {
            if (!isEmote())
                throw new IllegalStateException("Cannot get custom emote for emoji reaction");
            return emote;
        }


        @Nonnull
        public JDA getJDA() {
            return api;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReactionEmote
                    && Objects.equals(((ReactionEmote) obj).id, id)
                    && ((ReactionEmote) obj).getName().equals(name);
        }

        @Override
        public String toString() {
            if (isEmoji())
                return "RE:" + getAsCodepoints();
            return "RE:" + getName() + "(" + getId() + ")";
        }
    }
}
