package me.syari.ss.discord.internal.entities;

import gnu.trove.set.TLongSet;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceivedMessage extends AbstractMessage {
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

    // LAZY EVALUATED
    protected String altContent = null;

    protected List<User> userMentions = null;
    protected List<Emote> emoteMentions = null;
    protected List<Role> roleMentions = null;
    protected List<TextChannel> channelMentions = null;

    public ReceivedMessage(
            long id, MessageChannel channel, MessageType type,
            boolean fromWebhook, boolean mentionsEveryone, TLongSet mentionedUsers, TLongSet mentionedRoles, boolean tts,
            String content, User author, Member member,
            List<MessageEmbed> embeds) {
        super(content, tts);
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
    @Override
    public JDA getJDA() {
        return api;
    }


    @Nonnull
    @Override
    public MessageType getType() {
        return type;
    }

    @Override
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
    @Override
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
    @Override
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
    @Override
    public synchronized List<Role> getMentionedRoles() {
        if (roleMentions == null)
            roleMentions = Collections.unmodifiableList(processMentions(Message.MentionType.ROLE, new ArrayList<>(), this::matchRole));
        return roleMentions;
    }

    @Nonnull
    @Override
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

    @Override
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
    @Override
    public User getAuthor() {
        return author;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Nonnull
    @Override
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
                    name = getGuild().getMember(user).getEffectiveName();
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
    @Override
    public String getContentRaw() {
        return content;
    }

    @Override
    public boolean isFromType(@Nonnull ChannelType type) {
        return getChannelType() == type;
    }

    @Nonnull
    @Override
    public ChannelType getChannelType() {
        return channel.getType();
    }

    @Nonnull
    @Override
    public MessageChannel getChannel() {
        return channel;
    }

    @Nonnull
    @Override
    public PrivateChannel getPrivateChannel() {
        if (!isFromType(ChannelType.PRIVATE))
            throw new IllegalStateException("This message was not sent in a private channel");
        return (PrivateChannel) channel;
    }

    @Nonnull
    @Override
    public TextChannel getTextChannel() {
        if (!isFromType(ChannelType.TEXT))
            throw new IllegalStateException("This message was not sent in a text channel");
        return (TextChannel) channel;
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return getTextChannel().getGuild();
    }

    @Nonnull
    @Override
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
    @Override
    public synchronized List<Emote> getEmotes() {
        if (this.emoteMentions == null)
            emoteMentions = Collections.unmodifiableList(processMentions(Message.MentionType.EMOTE, new ArrayList<>(), this::matchEmote));
        return emoteMentions;
    }

    @Override
    public boolean isWebhookMessage() {
        return fromWebhook;
    }

    @Override
    public boolean isTTS() {
        return isTTS;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ReceivedMessage))
            return false;
        ReceivedMessage oMsg = (ReceivedMessage) o;
        return this.id == oMsg.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return author != null
                ? String.format("M:%#s:%.20s(%s)", author, this, getId())
                : String.format("M:%.20s", this); // this message was made using MessageBuilder
    }

    @Override
    protected void unsupported() {
        throw new UnsupportedOperationException("This operation is not supported on received messages!");
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;

        String out = alt ? getContentRaw() : getContentDisplay();

        if (upper)
            out = out.toUpperCase(formatter.locale());

        appendFormat(formatter, width, precision, leftJustified, out);
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

}
