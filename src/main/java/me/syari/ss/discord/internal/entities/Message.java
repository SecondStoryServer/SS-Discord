package me.syari.ss.discord.internal.entities;

import gnu.trove.set.TLongSet;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    public static int MAX_FILE_AMOUNT = 10;

    public static int MAX_CONTENT_LENGTH = 2000;

    private final Object mutex = new Object();

    protected final JDAImpl api;
    protected final long id;
    protected final MessageType type;
    protected final MessageChannel channel;
    protected final boolean fromWebhook;
    protected final boolean mentionsEveryone;
    protected final User author;
    protected final Member member;
    protected final List<MessageEmbed> embeds;
    protected final TLongSet mentionedUsers;
    protected final TLongSet mentionedRoles;
    protected final String content;
    protected final boolean isTTS;

    // LAZY EVALUATED
    protected String altContent = null;

    protected List<User> userMentions = null;
    protected List<Emote> emoteMentions = null;
    protected List<Role> roleMentions = null;
    protected List<TextChannel> channelMentions = null;

    public Message(
            long id, MessageChannel channel, MessageType type,
            boolean fromWebhook, boolean mentionsEveryone, TLongSet mentionedUsers, TLongSet mentionedRoles, boolean tts,
            String content, User author, Member member,
            List<MessageEmbed> embeds) {
        this.content = content;
        this.isTTS = tts;
        this.id = id;
        this.channel = channel;
        this.type = type;
        this.api = (channel != null) ? (JDAImpl) channel.getJDA() : null;
        this.fromWebhook = fromWebhook;
        this.mentionsEveryone = mentionsEveryone;
        this.author = author;
        this.member = member;
        this.embeds = Collections.unmodifiableList(embeds);
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
    }

    @Nonnull
    public JDA getJDA() {
        return api;
    }


    @Nonnull
    public MessageType getType() {
        return type;
    }

    public long getIdLong() {
        return id;
    }

    private User matchUser(Matcher matcher) {
        long userId = MiscUtil.parseSnowflake(matcher.group(1));
        if (!mentionedUsers.contains(userId))
            return null;
        User user = getJDA().getUserById(userId);
        if (user == null)
            user = api.getFakeUserMap().get(userId);
        if (user == null && userMentions != null)
            user = userMentions.stream().filter(it -> it.getIdLong() == userId).findFirst().orElse(null);
        return user;
    }

    @Nonnull
    public synchronized List<User> getMentionedUsers() {
        if (userMentions == null)
            userMentions = Collections.unmodifiableList(processMentions(Message.MentionType.USER, new ArrayList<>(), this::matchUser));
        return userMentions;
    }

    private TextChannel matchTextChannel(Matcher matcher) {
        long channelId = MiscUtil.parseSnowflake(matcher.group(1));
        return getJDA().getTextChannelById(channelId);
    }

    @Nonnull
    public synchronized List<TextChannel> getMentionedChannels() {
        if (channelMentions == null)
            channelMentions = Collections.unmodifiableList(processMentions(Message.MentionType.CHANNEL, new ArrayList<>(), this::matchTextChannel));
        return channelMentions;
    }

    private Role matchRole(Matcher matcher) {
        long roleId = MiscUtil.parseSnowflake(matcher.group(1));
        if (!mentionedRoles.contains(roleId))
            return null;
        if (getChannelType().isGuild())
            return getGuild().getRoleById(roleId);
        else
            return getJDA().getRoleById(roleId);
    }

    @Nonnull
    public synchronized List<Role> getMentionedRoles() {
        if (roleMentions == null)
            roleMentions = Collections.unmodifiableList(processMentions(Message.MentionType.ROLE, new ArrayList<>(), this::matchRole));
        return roleMentions;
    }

    @Nonnull
    public List<IMentionable> getMentions(@Nonnull Message.MentionType... types) {
        if (types.length == 0)
            return getMentions(Message.MentionType.values());
        List<IMentionable> mentions = new ArrayList<>();
        // boolean duplicate checks
        // not using Set because channel and role might have the same ID
        boolean channel = false;
        boolean role = false;
        boolean user = false;
        boolean emote = false;
        for (Message.MentionType type : types) {
            switch (type) {
                case EVERYONE:
                case HERE:
                default:
                    continue;
                case CHANNEL:
                    if (!channel)
                        mentions.addAll(getMentionedChannels());
                    channel = true;
                    break;
                case USER:
                    if (!user)
                        mentions.addAll(getMentionedUsers());
                    user = true;
                    break;
                case ROLE:
                    if (!role)
                        mentions.addAll(getMentionedRoles());
                    role = true;
                    break;
                case EMOTE:
                    if (!emote)
                        mentions.addAll(getEmotes());
                    emote = true;
            }
        }
        return Collections.unmodifiableList(mentions);
    }

    public boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull Message.MentionType... types) {
        Checks.notNull(types, "Mention Types");
        if (types.length == 0)
            return isMentioned(mentionable, Message.MentionType.values());
        final boolean isUserEntity = mentionable instanceof User || mentionable instanceof Member;
        for (Message.MentionType type : types) {
            switch (type) {
                case HERE: {
                    if (isMass("@here") && isUserEntity)
                        return true;
                    break;
                }
                case EVERYONE: {
                    if (isMass("@everyone") && isUserEntity)
                        return true;
                    break;
                }
                case USER: {
                    if (isUserMentioned(mentionable))
                        return true;
                    break;
                }
                case ROLE: {
                    if (isRoleMentioned(mentionable))
                        return true;
                    break;
                }
                case CHANNEL: {
                    if (mentionable instanceof TextChannel) {
                        if (getMentionedChannels().contains(mentionable))
                            return true;
                    }
                    break;
                }
                case EMOTE: {
                    if (mentionable instanceof Emote) {
                        if (getEmotes().contains(mentionable))
                            return true;
                    }
                    break;
                }
//              default: continue;
            }
        }
        return false;
    }

    private boolean isUserMentioned(IMentionable mentionable) {
        if (mentionable instanceof User) {
            return getMentionedUsers().contains(mentionable);
        } else if (mentionable instanceof Member) {
            final Member member = (Member) mentionable;
            return getMentionedUsers().contains(member.getUser());
        }
        return false;
    }

    private boolean isRoleMentioned(IMentionable mentionable) {
        if (mentionable instanceof Role) {
            return getMentionedRoles().contains(mentionable);
        } else if (mentionable instanceof Member) {
            final Member member = (Member) mentionable;
            return CollectionUtils.containsAny(getMentionedRoles(), member.getRoles());
        } else if (isFromType(ChannelType.TEXT) && mentionable instanceof User) {
            final Member member = getGuild().getMember((User) mentionable);
            return member != null && CollectionUtils.containsAny(getMentionedRoles(), member.getRoles());
        }
        return false;
    }

    private boolean isMass(String s) {
        return mentionsEveryone && content.contains(s);
    }

    @Nonnull
    public User getAuthor() {
        return author;
    }

    public Member getMember() {
        return member;
    }

    @Nonnull
    public String getContentDisplay() {
        if (altContent != null)
            return altContent;
        synchronized (mutex) {
            if (altContent != null)
                return altContent;
            String tmp = content;
            for (User user : getMentionedUsers()) {
                String name;
                if (isFromType(ChannelType.TEXT) && getGuild().isMember(user))
                    name = getGuild().getMember(user).getDisplayName();
                else
                    name = user.getName();
                tmp = tmp.replaceAll("<@!?" + Pattern.quote(user.getId()) + '>', '@' + Matcher.quoteReplacement(name));
            }
            for (Emote emote : getEmotes()) {
                tmp = tmp.replace(emote.getAsMention(), ":" + emote.getName() + ":");
            }
            for (TextChannel mentionedChannel : getMentionedChannels()) {
                tmp = tmp.replace(mentionedChannel.getAsMention(), '#' + mentionedChannel.getName());
            }
            for (Role mentionedRole : getMentionedRoles()) {
                tmp = tmp.replace(mentionedRole.getAsMention(), '@' + mentionedRole.getName());
            }
            return altContent = tmp;
        }
    }

    @Nonnull
    public String getContentRaw() {
        return content;
    }

    public boolean isFromType(@Nonnull ChannelType type) {
        return getChannelType() == type;
    }

    @Nonnull
    public ChannelType getChannelType() {
        return channel.getType();
    }

    @Nonnull
    public MessageChannel getChannel() {
        return channel;
    }

    @Nonnull
    public TextChannel getTextChannel() {
        if (!isFromType(ChannelType.TEXT))
            throw new IllegalStateException("This message was not sent in a text channel");
        return (TextChannel) channel;
    }

    @Nonnull
    public Guild getGuild() {
        return getTextChannel().getGuild();
    }

    @Nonnull
    public List<MessageEmbed> getEmbeds() {
        return embeds;
    }

    private Emote matchEmote(Matcher m) {
        long emoteId = MiscUtil.parseSnowflake(m.group(2));
        String name = m.group(1);
        boolean animated = m.group(0).startsWith("<a:");
        Emote emote = getJDA().getEmoteById(emoteId);
        if (emote == null)
            emote = new EmoteImpl(emoteId, api).setName(name).setAnimated(animated);
        return emote;
    }

    @Nonnull
    public synchronized List<Emote> getEmotes() {
        if (this.emoteMentions == null)
            emoteMentions = Collections.unmodifiableList(processMentions(Message.MentionType.EMOTE, new ArrayList<>(), this::matchEmote));
        return emoteMentions;
    }

    public boolean isTTS() {
        return isTTS;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Message))
            return false;
        Message oMsg = (Message) o;
        return this.id == oMsg.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return author != null
                ? String.format("M:%#s:%.20s(%s)", author, this, id)
                : String.format("M:%.20s", this); // this message was made using MessageBuilder
    }

    public void setMentions(List<User> users, List<Member> members) {
        users.sort(Comparator.comparing((user) ->
                Math.max(content.indexOf("<@" + user.getId() + ">"),
                        content.indexOf("<@!" + user.getId() + ">")
                )));
        members.sort(Comparator.comparing((user) ->
                Math.max(content.indexOf("<@" + user.getId() + ">"),
                        content.indexOf("<@!" + user.getId() + ">")
                )));

        this.userMentions = Collections.unmodifiableList(users);
    }

    private <T, C extends Collection<T>> C processMentions(MentionType type, C collection, Function<Matcher, T> map) {
        Matcher matcher = type.getPattern().matcher(getContentRaw());
        while (matcher.find()) {
            try {
                T elem = map.apply(matcher);
                if (elem == null || (collection.contains(elem)))
                    continue;
                collection.add(elem);
            } catch (NumberFormatException ignored) {
            }
        }
        return collection;
    }

    boolean isFromGuild() {
        return getChannelType().isGuild();
    }

    protected void appendFormat(Formatter formatter, int width, int precision, boolean leftJustified, String out) {
        try {
            Appendable appendable = formatter.out();
            if (precision > -1 && out.length() > precision) {
                appendable.append(Helpers.truncate(out, precision - 3)).append("...");
                return;
            }

            if (leftJustified)
                appendable.append(Helpers.rightPad(out, width));
            else
                appendable.append(Helpers.leftPad(out, width));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


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
}
