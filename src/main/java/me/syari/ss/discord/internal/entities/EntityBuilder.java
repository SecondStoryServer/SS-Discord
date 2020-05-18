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


import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;

import static me.syari.ss.discord.util.Check.isDefaultMessage;
import static me.syari.ss.discord.util.Check.isTextChannel;

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

    private void createGuildEmotePass(@NotNull Guild guildObj, @NotNull DataArray array) {
        SnowflakeCacheView<Emote> emoteView = guildObj.getEmotesView();
        try (UnlockHook hook = emoteView.writeLock()) {
            TLongObjectMap<Emote> emoteMap = emoteView.getMap();
            for (int i = 0; i < array.length(); i++) {
                DataObject object = array.getObject(i);
                if (object.isNull("id")) {
                    continue;
                }
                final long emoteId = object.getLong("id");
                emoteMap.put(emoteId, createEmote(guildObj, object));
            }
        }
    }

    public Guild createGuild(long guildId, @NotNull DataObject guildJson, int memberCount) {
        final Guild guildObj = new Guild(getJDA(), guildId);
        final String name = guildJson.getString("name", "");
        final DataArray roleArray = guildJson.getArray("roles");
        final DataArray channelArray = guildJson.getArray("channels");
        final DataArray emotesArray = guildJson.getArray("emojis");
        final long ownerId = guildJson.getUnsignedLong("owner_id", 0L);

        guildObj.setName(name);
        guildObj.setOwnerId(ownerId);
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

        createGuildEmotePass(guildObj, emotesArray);

        return guildObj;
    }

    private void createTextChannel(Guild guildObj, @NotNull DataObject channelData) {
        if (isTextChannel(channelData.getInt("type"))) {
            createTextChannel(guildObj, channelData, guildObj.getIdLong());
        }
    }

    public User createFakeUser(DataObject user, boolean modifyCache) {
        return createUser(user, true, modifyCache);
    }

    public User createUser(DataObject user) {
        return createUser(user, false, true);
    }

    private @NotNull User createUser(@NotNull DataObject user, boolean fake, boolean modifyCache) {
        final long id = user.getLong("id");
        User userObj;

        SnowflakeCacheView<User> userView = getJDA().getUsersView();
        try (UnlockHook hook = userView.writeLock()) {
            userObj = userView.getElementById(id);
            if (userObj == null) {
                userObj = getJDA().getFakeUserMap().get(id);
                if (userObj != null) {
                    if (!fake && modifyCache) {
                        getJDA().getFakeUserMap().remove(id);
                        userObj.setFake(false);
                        userView.getMap().put(userObj.getIdLong(), userObj);
                    }
                } else {
                    userObj = new User(id, getJDA()).setFake(fake);
                    // Cache user if guild subscriptions are enabled
                    if (modifyCache) {
                        if (fake)
                            getJDA().getFakeUserMap().put(id, userObj);
                        else
                            userView.getMap().put(id, userObj);
                    }
                }
            }
        }

        if (modifyCache || userObj.isFake()) {
            userObj.setName(user.getString("username"))
                    .setDiscriminator(user.get("discriminator").toString())
                    .setBot(user.getBoolean("bot"));
        } else if (!userObj.isFake()) {
            updateUser(userObj, user);
        }
        if (!fake && modifyCache) {
            getJDA().getEventCache().playbackCache(EventCache.Type.USER, id);
        }
        return userObj;
    }

    public void updateUser(@NotNull User userObj, @NotNull DataObject user) {
        String oldName = userObj.getName();
        String newName = user.getString("username");
        String oldDiscriminator = userObj.getDiscriminator();
        String newDiscriminator = user.get("discriminator").toString();

        if (!oldName.equals(newName)) {
            userObj.setName(newName);
        }

        if (!oldDiscriminator.equals(newDiscriminator)) {
            userObj.setDiscriminator(newDiscriminator);
        }
    }

    public Member createMember(@NotNull Guild guild, @NotNull DataObject memberJson) {
        boolean playbackCache = false;
        User user = createUser(memberJson.getObject("user"));
        Member member = guild.getMember(user);
        if (member == null) {
            MemberCacheView memberView = guild.getMembersView();
            try (UnlockHook hook = memberView.writeLock()) {
                member = new Member(guild, user);
                playbackCache = true;
            }
            if (guild.getOwnerIdLong() == user.getIdLong()) {
                guild.setOwner(member);
            }
        }

        if (playbackCache) {
            loadMember(guild, memberJson, member);
            long hashId = guild.getIdLong() ^ user.getIdLong();
            getJDA().getEventCache().playbackCache(EventCache.Type.MEMBER, hashId);
            guild.acknowledgeMembers();
        } else {
            DataArray roleArray = memberJson.getArray("roles");
            List<Role> roles = new ArrayList<>(roleArray.length());
            for (int i = 0; i < roleArray.length(); i++) {
                long roleId = roleArray.getUnsignedLong(i);
                Role role = guild.getRoleById(roleId);
                if (role != null)
                    roles.add(role);
            }
            updateMember(member, memberJson, roles);
        }
        return member;
    }

    private void loadMember(Guild guild, @NotNull DataObject memberJson, Member member) {
        if (!memberJson.isNull("premium_since")) {
            TemporalAccessor boostDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(memberJson.getString("premium_since"));
            member.setBoostDate(Instant.from(boostDate).toEpochMilli());
        }

        member.setNickname(memberJson.getString("nick", null));

        DataArray rolesJson = memberJson.getArray("roles");
        for (int k = 0; k < rolesJson.length(); k++) {
            final long roleId = rolesJson.getLong(k);
            Role role = guild.getRolesView().get(roleId);
            if (role == null) {
            } else {
                member.getRoleSet().add(role);
            }
        }
    }

    public void updateMember(Member member, DataObject content, List<Role> newRoles) {
        if (newRoles != null) {
            updateMemberRoles(member, newRoles);
        }
        if (content.hasKey("nick")) {
            String oldNick = member.getNickname();
            String newNick = content.getString("nick", null);
            if (!Objects.equals(oldNick, newNick)) {
                member.setNickname(newNick);
            }
        }
        if (content.hasKey("premium_since")) {
            long epoch = 0;
            if (!content.isNull("premium_since")) {
                TemporalAccessor date = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(content.getString("premium_since"));
                epoch = Instant.from(date).toEpochMilli();
            }
            if (epoch != member.getBoostDateRaw()) {
                member.setBoostDate(epoch);
            }
        }
    }

    private void updateMemberRoles(@NotNull Member member, List<Role> newRoles) {
        Set<Role> currentRoles = member.getRoleSet();
        List<Role> removedRoles = new LinkedList<>();
        each:
        for (Role role : currentRoles) {
            for (Iterator<Role> it = newRoles.iterator(); it.hasNext(); ) {
                Role r = it.next();
                if (role.equals(r)) {
                    it.remove();
                    continue each;
                }
            }
            removedRoles.add(role);
        }

        if (0 < removedRoles.size()) {
            currentRoles.removeAll(removedRoles);
        }
        if (0 < newRoles.size()) {
            currentRoles.addAll(newRoles);
        }
    }

    public Emote createEmote(@NotNull Guild guildObj, @NotNull DataObject json) {
        DataArray emoteRoles = json.optArray("roles").orElseGet(DataArray::empty);
        final long emoteId = json.getLong("id");
        Emote emoteObj = guildObj.getEmoteById(emoteId);
        if (emoteObj == null)
            emoteObj = new Emote(emoteId);
        Set<Role> roleSet = emoteObj.getRoleSet();

        roleSet.clear();
        for (int j = 0; j < emoteRoles.length(); j++) {
            Role role = guildObj.getRoleById(emoteRoles.getString(j));
            if (role != null)
                roleSet.add(role);
        }
        return emoteObj
                .setName(json.getString("name", ""))
                .setAnimated(json.getBoolean("animated"));
    }


    public void createTextChannel(Guild guildObj, @NotNull DataObject json, long guildId) {
        boolean playbackCache = false;
        final long id = json.getLong("id");
        TextChannel channel = getJDA().getTextChannelsView().get(id);
        if (channel == null) {
            if (guildObj == null)
                guildObj = getJDA().getGuildsView().get(guildId);
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
        if (playbackCache) {
            getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
        }
    }

    public Role createRole(Guild guild, @NotNull DataObject roleJson, long guildId) {
        boolean playbackCache = false;
        final long id = roleJson.getLong("id");
        if (guild == null) {
            guild = getJDA().getGuildsView().get(guildId);
        }
        Role role = guild.getRolesView().get(id);
        if (role == null) {
            SnowflakeCacheView<Role> roleView = guild.getRolesView();
            try (UnlockHook hook = roleView.writeLock()) {
                role = new Role(id);
                playbackCache = roleView.getMap().put(id, role) == null;
            }
        }
        role.setName(roleJson.getString("name"));
        if (playbackCache) {
            getJDA().getEventCache().playbackCache(EventCache.Type.ROLE, id);
        }
        return role;
    }

    public Message createMessage(@NotNull DataObject jsonObject, boolean modifyCache) {
        final long channelId = jsonObject.getLong("channel_id");

        TextChannel channel = getJDA().getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException(MISSING_CHANNEL);
        }

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
                user = createFakeUser(author, false);
            } else {
                throw new IllegalArgumentException(MISSING_USER);
            }
        }

        if (modifyCache && !fromWebhook)
            updateUser(user, author);

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

        if (guildImpl.isLoaded())
            return message;

        List<User> mentionedUsersList = new ArrayList<>();
        List<Member> mentionedMembersList = new ArrayList<>();
        DataArray userMentions = jsonObject.getArray("mentions");

        for (int i = 0; i < userMentions.length(); i++) {
            DataObject mentionJson = userMentions.getObject(i);
            if (mentionJson.isNull("member")) {
                User mentionedUser = createFakeUser(mentionJson, false);
                mentionedUsersList.add(mentionedUser);
                Member mentionedMember = guildImpl.getMember(mentionedUser);
                if (mentionedMember != null) {
                    mentionedMembersList.add(mentionedMember);
                }
            } else {
                DataObject memberJson = mentionJson.getObject("member");
                mentionJson.remove("member");
                memberJson.put("user", mentionJson);
                Member mentionedMember = createMember(guildImpl, memberJson);
                mentionedMembersList.add(mentionedMember);
                mentionedUsersList.add(mentionedMember.getUser());
            }
        }

        if (!mentionedUsersList.isEmpty())
            message.setMentions(mentionedUsersList, mentionedMembersList);
        return message;
    }

    private <T> @NotNull List<T> map(@NotNull DataObject jsonObject, String key, Function<DataObject, T> convert) {
        if (jsonObject.isNull(key))
            return Collections.emptyList();

        final DataArray array = jsonObject.getArray(key);
        final List<T> mappedObjects = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            DataObject obj = array.getObject(i);
            T result = convert.apply(obj);
            if (result != null) {
                mappedObjects.add(result);
            }
        }

        return mappedObjects;
    }
}
