package me.syari.ss.discord.internal.entities;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.handle.EventCache;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.MemberCacheView;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

import static me.syari.ss.discord.internal.utils.Check.isDefaultMessage;
import static me.syari.ss.discord.internal.utils.Check.isTextChannel;

public class EntityBuilder {
    public static final String MISSING_CHANNEL = "MISSING_CHANNEL";
    public static final String MISSING_USER = "MISSING_USER";
    public static final String UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE";

    protected final JDA api;

    public EntityBuilder(JDA api) {
        this.api = api;
    }

    public JDA getJDA() {
        return api;
    }

    private void createTextChannel(Guild guildObj, @NotNull DataObject channelData) {
        if (isTextChannel(channelData.getInt("type"))) {
            createTextChannel(guildObj, channelData, guildObj.getIdLong());
        }
    }

    public Guild createGuild(long guildId, @NotNull DataObject guildJson, int memberCount) {
        final Guild guildObj = new Guild(getJDA(), guildId);
        final String name = guildJson.getString("name", "");
        final DataArray roleArray = guildJson.getArray("roles");
        final DataArray channelArray = guildJson.getArray("channels");

        guildObj.setName(name);
        guildObj.setMemberCount(memberCount);

        SnowflakeCacheView<Guild> guildView = getJDA().getGuildsView();
        try (UnlockHook hook = guildView.writeLock()) {
            guildView.getMap().put(guildId, guildObj);
        }

        SnowflakeCacheView<Role> roleView = guildObj.getRolesView();
        try (UnlockHook hook = roleView.writeLock()) {
            TLongObjectMap<Role> map = roleView.getMap();
            for (int i = 0; i < roleArray.length(); i++) {
                DataObject obj = roleArray.getObject(i);
                Role role = createRole(guildObj, obj, guildId);
                map.put(role.getIdLong(), role);
            }
        }

        for (int i = 0; i < channelArray.length(); i++) {
            DataObject channelJson = channelArray.getObject(i);
            createTextChannel(guildObj, channelJson);
        }
        return guildObj;
    }

    private @NotNull User createFakeUser(DataObject user) {
        return createUser(user, true);
    }

    private @NotNull User createUser(DataObject user) {
        return createUser(user, false);
    }

    private @NotNull User createUser(@NotNull DataObject userData, boolean fake) {
        final long id = userData.getLong("id");
        User user;
        SnowflakeCacheView<User> userView = getJDA().getUsersView();
        try (UnlockHook hook = userView.writeLock()) {
            user = userView.getElementById(id);
            if (user == null) {
                user = getJDA().getFakeUserMap().get(id);
                if (user != null) {
                    if (!fake) {
                        getJDA().getFakeUserMap().remove(id);
                        user.setFake(false);
                        userView.getMap().put(user.getIdLong(), user);
                    }
                } else {
                    user = new User(id, getJDA());
                    user.setFake(fake);
                    if (!fake) userView.getMap().put(id, user);
                }
            }
        }

        if (!fake || user.isFake()) {
            user.setName(userData.getString("username"));
            user.setDiscriminator(userData.get("discriminator").toString());
            user.setBot(userData.getBoolean("bot"));
        } else if (!user.isFake()) {
            updateUser(user, userData);
        }
        if (!fake) getJDA().getEventCache().playbackCache(EventCache.Type.USER, id);
        return user;
    }

    private void updateUser(@NotNull User user, @NotNull DataObject userData) {
        String lastName = user.getName();
        String name = userData.getString("username");
        String lastDiscriminator = user.getDiscriminator();
        String discriminator = userData.get("discriminator").toString();

        if (!name.equals(lastName)) {
            user.setName(name);
        }

        if (!discriminator.equals(lastDiscriminator)) {
            user.setDiscriminator(discriminator);
        }
    }

    private Member createMember(@NotNull Guild guild, @NotNull DataObject memberJson) {
        boolean playbackCache = false;
        User user = createUser(memberJson.getObject("user"));
        Member member = guild.getMember(user);
        if (member == null) {
            MemberCacheView memberView = guild.getMembersView();
            try (UnlockHook hook = memberView.writeLock()) {
                member = new Member(guild, user);
                playbackCache = true;
            }
        }

        if (playbackCache) {
            loadMember(memberJson, member);
            long hashId = guild.getIdLong() ^ user.getIdLong();
            getJDA().getEventCache().playbackCache(EventCache.Type.MEMBER, hashId);
            guild.acknowledgeMembers();
        } else {
            updateMember(member, memberJson);
        }
        return member;
    }

    private void loadMember(@NotNull DataObject memberJson, @NotNull Member member) {
        member.setNickname(memberJson.getString("nick", null));
    }

    private void updateMember(Member member, @NotNull DataObject content) {
        if (content.hasKey("nick")) {
            String lastNickName = member.getNickname();
            String nickName = content.getString("nick", null);
            if (!Objects.equals(nickName, lastNickName)) {
                member.setNickname(nickName);
            }
        }
    }

    private void createTextChannel(Guild guildObj, @NotNull DataObject json, long guildId) {
        boolean playbackCache = false;
        final long id = json.getLong("id");
        TextChannel channel = getJDA().getTextChannelsView().get(id);
        if (channel == null) {
            if (guildObj == null) guildObj = getJDA().getGuildsView().get(guildId);
            SnowflakeCacheView<TextChannel>
                    guildTextView = guildObj.getTextChannelsView(),
                    textView = getJDA().getTextChannelsView();
            try (
                    UnlockHook glock = guildTextView.writeLock();
                    UnlockHook jlock = textView.writeLock()) {
                channel = new TextChannel(id, guildObj);
                guildTextView.getMap().put(id, channel);
                playbackCache = textView.getMap().put(id, channel) == null;
            }
        }

        channel.setName(json.getString("name"));
        if (playbackCache) getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
    }

    private @NotNull Role createRole(Guild guild, @NotNull DataObject roleJson, long guildId) {
        boolean playbackCache = false;
        final long id = roleJson.getLong("id");
        if (guild == null) guild = getJDA().getGuildsView().get(guildId);
        Role role = guild.getRolesView().get(id);
        if (role == null) {
            SnowflakeCacheView<Role> roleView = guild.getRolesView();
            try (UnlockHook hook = roleView.writeLock()) {
                role = new Role(id);
                playbackCache = roleView.getMap().put(id, role) == null;
            }
        }
        role.setName(roleJson.getString("name"));
        if (playbackCache) getJDA().getEventCache().playbackCache(EventCache.Type.ROLE, id);
        return role;
    }

    public Message createMessage(@NotNull DataObject jsonObject, boolean modifyCache) {
        final long channelId = jsonObject.getLong("channel_id");
        TextChannel channel = getJDA().getTextChannelById(channelId);
        if (channel == null) throw new IllegalArgumentException(MISSING_CHANNEL);
        return createMessage(jsonObject, channel, modifyCache);
    }

    public Message createMessage(@NotNull DataObject jsonObject, @NotNull TextChannel channel, boolean modifyCache) {
        final long id = jsonObject.getLong("id");
        final DataObject author = jsonObject.getObject("author");
        final long authorId = author.getLong("id");
        Member member = null;

        if (!jsonObject.isNull("member") && modifyCache) {
            Guild guild = channel.getGuild();
            Member cachedMember = guild.getMemberById(authorId);
            if (cachedMember == null) {
                DataObject memberJson = jsonObject.getObject("member");
                memberJson.put("user", author);
                member = createMember(guild, memberJson);
            } else {
                member = cachedMember;
            }
        }

        final String content = jsonObject.getString("content", "");
        final boolean fromWebhook = jsonObject.hasKey("webhook_id");

        User user;
        Guild guild = channel.getGuild();
        if (member == null)
            member = guild.getMemberById(authorId);
        user = member != null ? member.getUser() : null;
        if (user == null) {
            if (fromWebhook || !modifyCache) {
                user = createFakeUser(author);
            } else {
                throw new IllegalArgumentException(MISSING_USER);
            }
        }

        if (modifyCache && !fromWebhook) updateUser(user, author);

        TLongSet mentionedRoles = new TLongHashSet();
        TLongSet mentionedUsers = new TLongHashSet(map(jsonObject, "mentions", (o) -> o.getLong("id")));
        Optional<DataArray> roleMentionArray = jsonObject.optArray("mention_roles");
        roleMentionArray.ifPresent((array) ->
        {
            for (int i = 0; i < array.length(); i++) {
                mentionedRoles.add(array.getLong(i));
            }
        });

        Message message;
        if (isDefaultMessage(jsonObject.getInt("type"))) {
            message = new Message(id, channel, mentionedUsers, mentionedRoles, content, user, member);
        } else {
            throw new IllegalArgumentException(UNKNOWN_MESSAGE_TYPE);
        }

        Guild guildImpl = message.getGuild();

        if (guildImpl.isLoaded()) return message;

        List<User> mentionedUsersList = new ArrayList<>();
        List<Member> mentionedMembersList = new ArrayList<>();
        DataArray userMentions = jsonObject.getArray("mentions");

        for (int i = 0; i < userMentions.length(); i++) {
            DataObject mentionJson = userMentions.getObject(i);
            if (mentionJson.isNull("member")) {
                User mentionedUser = createFakeUser(mentionJson);
                mentionedUsersList.add(mentionedUser);
                Member mentionedMember = guildImpl.getMember(mentionedUser);
                if (mentionedMember != null) mentionedMembersList.add(mentionedMember);
            } else {
                DataObject memberJson = mentionJson.getObject("member");
                mentionJson.remove("member");
                memberJson.put("user", mentionJson);
                Member mentionedMember = createMember(guildImpl, memberJson);
                mentionedMembersList.add(mentionedMember);
                mentionedUsersList.add(mentionedMember.getUser());
            }
        }

        if (!mentionedUsersList.isEmpty()) message.setMentions(mentionedUsersList, mentionedMembersList);
        return message;
    }

    private <T> @NotNull List<T> map(@NotNull DataObject jsonObject, String key, Function<DataObject, T> convert) {
        if (jsonObject.isNull(key)) return Collections.emptyList();
        final DataArray array = jsonObject.getArray(key);
        final List<T> mappedObjects = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            DataObject obj = array.getObject(i);
            T result = convert.apply(obj);
            if (result != null) mappedObjects.add(result);
        }
        return mappedObjects;
    }
}
