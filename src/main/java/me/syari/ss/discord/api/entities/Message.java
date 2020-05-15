
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.MessageBuilder;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.events.message.MessageUpdateEvent;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.managers.AccountManager;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.utils.AttachmentOption;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.HttpException;
import me.syari.ss.discord.api.requests.restaction.pagination.ReactionPaginationAction;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.FunctionalCallback;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.collections4.Bag;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public interface Message extends ISnowflake, Formattable
{

    int MAX_FILE_SIZE = 8 << 20;


    int MAX_FILE_SIZE_NITRO = 50 << 20;


    int MAX_FILE_AMOUNT = 10;


    int MAX_CONTENT_LENGTH = 2000;


    Pattern INVITE_PATTERN = Pattern.compile("(?:https?://)?discord(?:app\\.com/invite|\\.gg)/([a-z0-9-]+)", Pattern.CASE_INSENSITIVE);


    @Nonnull
    List<User> getMentionedUsers();


    @Nonnull
    Bag<User> getMentionedUsersBag();


    @Nonnull
    List<TextChannel> getMentionedChannels();


    @Nonnull
    Bag<TextChannel> getMentionedChannelsBag();


    @Nonnull
    List<Role> getMentionedRoles();


    @Nonnull
    Bag<Role> getMentionedRolesBag();


    @Nonnull
    List<Member> getMentionedMembers(@Nonnull Guild guild);


    @Nonnull
    List<Member> getMentionedMembers();


    @Nonnull
    List<IMentionable> getMentions(@Nonnull MentionType... types);


    boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull MentionType... types);


    boolean mentionsEveryone();


    boolean isEdited();


    @Nullable
    OffsetDateTime getTimeEdited();


    @Nonnull
    User getAuthor();


    @Nullable
    Member getMember();


    @Nonnull
    String getJumpUrl();


    @Nonnull
    String getContentDisplay();


    @Nonnull
    String getContentRaw();


    @Nonnull
    String getContentStripped();


    @Nonnull
    List<String> getInvites();


    @Nullable
    String getNonce();


    boolean isFromType(@Nonnull ChannelType type);


    default boolean isFromGuild()
    {
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


    @Nullable
    Category getCategory();


    @Nonnull
    Guild getGuild();


    @Nonnull
    List<Attachment> getAttachments();


    @Nonnull
    List<MessageEmbed> getEmbeds();


    @Nonnull
    List<Emote> getEmotes();


    @Nonnull
    Bag<Emote> getEmotesBag();


    @Nonnull
    List<MessageReaction> getReactions();


    boolean isTTS();


    @Nullable
    MessageActivity getActivity();


    @Nonnull
    @CheckReturnValue
    MessageAction editMessage(@Nonnull CharSequence newContent);


    @Nonnull
    @CheckReturnValue
    MessageAction editMessage(@Nonnull MessageEmbed newContent);


    @Nonnull
    @CheckReturnValue
    MessageAction editMessageFormat(@Nonnull String format, @Nonnull Object... args);


    @Nonnull
    @CheckReturnValue
    MessageAction editMessage(@Nonnull Message newContent);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    JDA getJDA();


    boolean isPinned();


    @Nonnull
    @CheckReturnValue
    RestAction<Void> pin();


    @Nonnull
    @CheckReturnValue
    RestAction<Void> unpin();


    @Nonnull
    @CheckReturnValue
    RestAction<Void> addReaction(@Nonnull Emote emote);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> addReaction(@Nonnull String unicode);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> clearReactions();


    @Nonnull
    @CheckReturnValue
    RestAction<Void> removeReaction(@Nonnull Emote emote);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> removeReaction(@Nonnull Emote emote, @Nonnull User user);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> removeReaction(@Nonnull String unicode);


    @Nonnull
    @CheckReturnValue
    RestAction<Void> removeReaction(@Nonnull String unicode, @Nonnull User user);


    @Nonnull
    @CheckReturnValue
    ReactionPaginationAction retrieveReactionUsers(@Nonnull Emote emote);


    @Nonnull
    @CheckReturnValue
    ReactionPaginationAction retrieveReactionUsers(@Nonnull String unicode);


    @Nullable
    @CheckReturnValue
    MessageReaction.ReactionEmote getReactionByUnicode(@Nonnull String unicode);


    @Nullable
    @CheckReturnValue
    MessageReaction.ReactionEmote getReactionById(@Nonnull String id);


    @Nullable
    @CheckReturnValue
    MessageReaction.ReactionEmote getReactionById(long id);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> suppressEmbeds(boolean suppressed);


    boolean isSuppressedEmbeds();


    @Nonnull
    EnumSet<MessageFlag> getFlags();


    @Nonnull
    MessageType getType();


    enum MentionType
    {

        USER("<@!?(\\d+)>"),

        ROLE("<@&(\\d+)>"),

        CHANNEL("<#(\\d+)>"),

        EMOTE("<a?:([a-zA-Z0-9_]+):([0-9]+)>"),

        HERE("@here"),

        EVERYONE("@everyone");

        private final Pattern pattern;

        MentionType(String regex)
        {
            this.pattern = Pattern.compile(regex);
        }

        @Nonnull
        public Pattern getPattern()
        {
            return pattern;
        }
    }


    enum MessageFlag
    {

        CROSSPOSTED(0),

        IS_CROSSPOST(1),

        EMBEDS_SUPPRESSED(2),

        SOURCE_MESSAGE_DELETED(3),

        URGENT(4);

        private final int value;

        MessageFlag(int offset)
        {
            this.value = 1 << offset;
        }


        public int getValue()
        {
            return value;
        }


        @Nonnull
        public static EnumSet<MessageFlag> fromBitField(int bitfield)
        {
            Set<MessageFlag> set = Arrays.stream(MessageFlag.values())
                .filter(e -> (e.value & bitfield) > 0)
                .collect(Collectors.toSet());
            return set.isEmpty() ? EnumSet.noneOf(MessageFlag.class) : EnumSet.copyOf(set);
        }


        public static int toBitField(@Nonnull Collection<MessageFlag> coll)
        {
            Checks.notNull(coll, "Collection");
            int flags = 0;
            for (MessageFlag messageFlag : coll)
            {
                flags |= messageFlag.value;
            }
            return flags;
        }
    }


    class Attachment implements ISnowflake
    {
        private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("jpg",
                "jpeg", "png", "gif", "webp", "tiff", "svg", "apng"));
        private static final Set<String> VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList("webm",
                "flv", "vob", "avi", "mov", "wmv", "amv", "mp4", "mpg", "mpeg", "gifv"));
        private final long id;
        private final String url;
        private final String proxyUrl;
        private final String fileName;
        private final int size;
        private final int height;
        private final int width;

        private final JDAImpl jda;

        public Attachment(long id, String url, String proxyUrl, String fileName, int size, int height, int width, JDAImpl jda)
        {
            this.id = id;
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.fileName = fileName;
            this.size = size;
            this.height = height;
            this.width = width;
            this.jda = jda;
        }


        @Nonnull
        public JDA getJDA()
        {
            return jda;
        }

        @Override
        public long getIdLong()
        {
            return id;
        }


        @Nonnull
        public String getUrl()
        {
            return url;
        }


        @Nonnull
        public String getProxyUrl()
        {
            return proxyUrl;
        }


        @Nonnull
        public String getFileName()
        {
            return fileName;
        }


        @Nullable
        public String getFileExtension()
        {
            int index = fileName.lastIndexOf('.') + 1;
            return index == 0 || index == fileName.length() ? null : fileName.substring(index);
        }


        @Nonnull
        public CompletableFuture<InputStream> retrieveInputStream() // it is expected that the response is closed by the callback!
        {
            CompletableFuture<InputStream> future = new CompletableFuture<>();
            Request req = getRequest();
            OkHttpClient httpClient = getJDA().getHttpClient();
            httpClient.newCall(req).enqueue(FunctionalCallback
                .onFailure((call, e) -> future.completeExceptionally(new UncheckedIOException(e)))
                .onSuccess((call, response) -> {
                    if (response.isSuccessful())
                    {
                        InputStream body = IOUtil.getBody(response);
                        if (!future.complete(body))
                            IOUtil.silentClose(response);
                    }
                    else
                    {
                        future.completeExceptionally(new HttpException(response.code() + ": " + response.message()));
                        IOUtil.silentClose(response);
                    }
                }).build());
            return future;
        }


        @Nonnull
        public CompletableFuture<File> downloadToFile() // using relative path
        {
            return downloadToFile(getFileName());
        }


        @Nonnull
        public CompletableFuture<File> downloadToFile(String path)
        {
            Checks.notNull(path, "Path");
            return downloadToFile(new File(path));
        }


        @Nonnull
        public CompletableFuture<File> downloadToFile(File file)
        {
            Checks.notNull(file, "File");
            Checks.check(!file.exists() || file.canWrite(), "Cannot write to file %s", file.getName());
            return retrieveInputStream().thenApplyAsync((stream) -> {
                try (FileOutputStream out = new FileOutputStream(file))
                {
                    byte[] buf = new byte[1024];
                    int count;
                    while ((count = stream.read(buf)) > 0)
                    {
                        out.write(buf, 0, count);
                    }
                    return file;
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
                finally
                {
                    IOUtil.silentClose(stream);
                }
            }, getJDA().getCallbackPool());
        }


        @Nonnull
        public CompletableFuture<Icon> retrieveAsIcon()
        {
            if (!isImage())
                throw new IllegalStateException("Cannot create an Icon out of this attachment. This is not an image.");
            return retrieveInputStream().thenApplyAsync((stream) ->
            {
                try
                {
                    return Icon.from(stream);
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
                finally
                {
                    IOUtil.silentClose(stream);
                }
            }, getJDA().getCallbackPool());
        }

        protected Request getRequest()
        {
            return new Request.Builder()
                .url(getUrl())
                .addHeader("user-agent", Requester.USER_AGENT)
                .addHeader("accept-encoding", "gzip, deflate")
                .build();
        }


        public int getSize()
        {
            return size;
        }


        public int getHeight()
        {
            return height;
        }


        public int getWidth()
        {
            return width;
        }


        public boolean isImage()
        {
            if (width < 0) return false; //if width is -1, so is height
            String extension = getFileExtension();
            return extension != null && IMAGE_EXTENSIONS.contains(extension.toLowerCase());
        }


        public boolean isVideo()
        {
            if (width < 0) return false; //if width is -1, so is height
            String extension = getFileExtension();
            return extension != null && VIDEO_EXTENSIONS.contains(extension.toLowerCase());
        }
    }
}
