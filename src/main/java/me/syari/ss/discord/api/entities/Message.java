package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Formattable;
import java.util.List;
import java.util.regex.Pattern;


public interface Message extends ISnowflake, Formattable {

    int MAX_FILE_SIZE = 8 << 20;


    int MAX_FILE_SIZE_NITRO = 50 << 20;


    int MAX_FILE_AMOUNT = 10;


    int MAX_CONTENT_LENGTH = 2000;


    @Nonnull
    List<User> getMentionedUsers();


    @Nonnull
    List<TextChannel> getMentionedChannels();


    @Nonnull
    List<Role> getMentionedRoles();


    @Nonnull
    List<IMentionable> getMentions(@Nonnull MentionType... types);


    boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull MentionType... types);


    @Nonnull
    User getAuthor();


    @Nullable
    Member getMember();


    @Nonnull
    String getContentDisplay();


    @Nonnull
    String getContentRaw();


    boolean isFromType(@Nonnull ChannelType type);


    default boolean isFromGuild() {
        return getChannelType().isGuild();
    }


    @Nonnull
    ChannelType getChannelType();


    boolean isWebhookMessage();


    @Nonnull
    MessageChannel getChannel();


    @Nonnull
    PrivateChannel getPrivateChannel();


    @Nonnull
    TextChannel getTextChannel();


    @Nonnull
    Guild getGuild();


    @Nonnull
    List<MessageEmbed> getEmbeds();


    @Nonnull
    List<Emote> getEmotes();


    boolean isTTS();


    @Nonnull
    JDA getJDA();


    @Nonnull
    MessageType getType();


    enum MentionType {

        USER("<@!?(\\d+)>"),

        ROLE("<@&(\\d+)>"),

        CHANNEL("<#(\\d+)>"),

        EMOTE("<a?:([a-zA-Z0-9_]+):([0-9]+)>"),

        HERE("@here"),

        EVERYONE("@everyone");

        private final Pattern pattern;

        MentionType(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        @Nonnull
        public Pattern getPattern() {
            return pattern;
        }
    }


    class Attachment implements ISnowflake {
        private final long id;

        public Attachment(long id) {
            this.id = id;
        }


        @Override
        public long getIdLong() {
            return id;
        }


    }
}
