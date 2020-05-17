package me.syari.ss.discord.internal.entities;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.handle.EventCache;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.MemberCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;

public class EntityBuilder {
    public static final Logger LOG = JDALogger.getLog(EntityBuilder.class);
    public static final String MISSING_CHANNEL = "MISSING_CHANNEL";
    public static final String MISSING_USER = "MISSING_USER";
    public static final String UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE";

    protected final JDAImpl api;

    public EntityBuilder(JDA api) {
        this.api = (JDAImpl) api;
    }

    public JDAImpl getJDA() {
        return api;
    }

    private void createGuildEmotePass(GuildImpl guildObj, DataArray array) {
        SnowflakeCacheViewImpl<Emote> emoteView = guildObj.getEmotesView();
        try (UnlockHook hook = emoteView.writeLock()) {
            TLongObjectMap<Emote> emoteMap = emoteView.getMap();
            for (int i = 0; i < array.length(); i++) {
                DataObject object = array.getObject(i);
                if (object.isNull("id")) {
                    LOG.error("Received GUILD_CREATE with an emoji with a null ID. JSON: {}", object);
                    continue;
                }
                final long emoteId = object.getLong("id");
                emoteMap.put(emoteId, createEmote(guildObj, object));
            }
        }
    }

    public GuildImpl createGuild(long guildId, DataObject guildJson, int memberCount) {
        final GuildImpl guildObj = new GuildImpl(getJDA(), guildId);
        final String name = guildJson.getString("name", "");
        final DataArray roleArray = guildJson.getArray("roles");
        final DataArray channelArray = guildJson.getArray("channels");
        final DataArray emotesArray = guildJson.getArray("emojis");
        final long ownerId = guildJson.getUnsignedLong("owner_id", 0L);

        guildObj.setName(name)
                .setOwnerId(ownerId)
                .setMemberCount(memberCount);

        SnowflakeCacheViewImpl<Guild> guildView = getJDA().getGuildsView();
        try (UnlockHook hook = guildView.writeLock()) {
            guildView.getMap().put(guildId, guildObj);
        }

        SnowflakeCacheViewImpl<Role> roleView = guildObj.getRolesView();
        try (UnlockHook hook = roleView.writeLock()) {
            TLongObjectMap<Role> map = roleView.getMap();
            for (int i = 0; i < roleArray.length(); i++) {
                DataObject obj = roleArray.getObject(i);
                Role role = createRole(guildObj, obj, guildId);
                map.put(role.getIdLong(), role);
            }
        }

        if (guildObj.getOwner() == null)
            LOG.debug("Finished setup for guild with a null owner. GuildId: {} OwnerId: {}", guildId, guildJson.opt("owner_id").orElse(null));

        for (int i = 0; i < channelArray.length(); i++) {
            DataObject channelJson = channelArray.getObject(i);
            createGuildChannel(guildObj, channelJson);
        }

        createGuildEmotePass(guildObj, emotesArray);

        return guildObj;
    }

    private void createGuildChannel(GuildImpl guildObj, DataObject channelData) {
        final ChannelType channelType = ChannelType.fromId(channelData.getInt("type"));
        if (channelType == ChannelType.TEXT) {
            createTextChannel(guildObj, channelData, guildObj.getIdLong());
        } else {
            LOG.debug("Cannot create channel for type " + channelData.getInt("type"));
        }
    }

    public UserImpl createFakeUser(DataObject user, boolean modifyCache) {
        return createUser(user, true, modifyCache);
    }

    public UserImpl createUser(DataObject user) {
        return createUser(user, false, true);
    }

    private UserImpl createUser(DataObject user, boolean fake, boolean modifyCache) {
        final long id = user.getLong("id");
        UserImpl userObj;

        SnowflakeCacheViewImpl<User> userView = getJDA().getUsersView();
        try (UnlockHook hook = userView.writeLock()) {
            userObj = (UserImpl) userView.getElementById(id);
            if (userObj == null) {
                userObj = (UserImpl) getJDA().getFakeUserMap().get(id);
                if (userObj != null) {
                    if (!fake && modifyCache) {
                        getJDA().getFakeUserMap().remove(id);
                        userObj.setFake(false);
                        userView.getMap().put(userObj.getIdLong(), userObj);
                    }
                } else {
                    userObj = new UserImpl(id, getJDA()).setFake(fake);
                    // Cache user if guild subscriptions are enabled
                    if (modifyCache && getJDA().isGuildSubscriptions()) {
                        if (fake)
                            getJDA().getFakeUserMap().put(id, userObj);
                        else
                            userView.getMap().put(id, userObj);
                    }
                }
            }
        }

        if (modifyCache || userObj.isFake()) {
            // Initial creation
            userObj.setName(user.getString("username"))
                    .setDiscriminator(user.get("discriminator").toString())
                    .setBot(user.getBoolean("bot"));
        } else if (!userObj.isFake()) {
            // Fire update events
            updateUser(userObj, user);
        }
        if (!fake && modifyCache)
            getJDA().getEventCache().playbackCache(EventCache.Type.USER, id);
        return userObj;
    }

    public void updateUser(UserImpl userObj, DataObject user) {
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

    public MemberImpl createMember(GuildImpl guild, DataObject memberJson) {
        boolean playbackCache = false;
        User user = createUser(memberJson.getObject("user"));
        MemberImpl member = (MemberImpl) guild.getMember(user);
        if (member == null) {
            MemberCacheViewImpl memberView = guild.getMembersView();
            try (UnlockHook hook = memberView.writeLock()) {
                member = new MemberImpl(guild, user);
                playbackCache = true;
            }
            if (guild.getOwnerIdLong() == user.getIdLong()) {
                LOG.trace("Found owner of guild with id {}", guild.getId());
                guild.setOwner(member);
            }
        }

        if (playbackCache) {
            loadMember(guild, memberJson, member);
            long hashId = guild.getIdLong() ^ user.getIdLong();
            getJDA().getEventCache().playbackCache(EventCache.Type.MEMBER, hashId);
            guild.acknowledgeMembers();
        } else {
            // This is not a new member - fire update events
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

    private void loadMember(GuildImpl guild, DataObject memberJson, MemberImpl member) {
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
                LOG.debug("Received a Member with an unknown Role. MemberId: {} GuildId: {} roleId: {}",
                        member.getUser().getId(), guild.getId(), roleId);
            } else {
                member.getRoleSet().add(role);
            }
        }
    }

    public void updateMember(MemberImpl member, DataObject content, List<Role> newRoles) {
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

    private void updateMemberRoles(MemberImpl member, List<Role> newRoles) {
        Set<Role> currentRoles = member.getRoleSet();
        //Find the roles removed.
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

        if (removedRoles.size() > 0)
            currentRoles.removeAll(removedRoles);
        if (newRoles.size() > 0)
            currentRoles.addAll(newRoles);
    }

    public EmoteImpl createEmote(GuildImpl guildObj, DataObject json) {
        DataArray emoteRoles = json.optArray("roles").orElseGet(DataArray::empty);
        final long emoteId = json.getLong("id");
        EmoteImpl emoteObj = (EmoteImpl) guildObj.getEmoteById(emoteId);
        if (emoteObj == null)
            emoteObj = new EmoteImpl(emoteId);
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


    public void createTextChannel(GuildImpl guildObj, DataObject json, long guildId) {
        boolean playbackCache = false;
        final long id = json.getLong("id");
        TextChannelImpl channel = (TextChannelImpl) getJDA().getTextChannelsView().get(id);
        if (channel == null) {
            if (guildObj == null)
                guildObj = (GuildImpl) getJDA().getGuildsView().get(guildId);
            SnowflakeCacheViewImpl<TextChannel>
                    guildTextView = guildObj.getTextChannelsView(),
                    textView = getJDA().getTextChannelsView();
            try (
                    UnlockHook glock = guildTextView.writeLock();
                    UnlockHook jlock = textView.writeLock()) {
                channel = new TextChannelImpl(id, guildObj);
                guildTextView.getMap().put(id, channel);
                playbackCache = textView.getMap().put(id, channel) == null;
            }
        }

        channel.setName(json.getString("name"))
                .setPosition(json.getInt("position"));
        if (playbackCache)
            getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
    }

    public Role createRole(GuildImpl guild, DataObject roleJson, long guildId) {
        boolean playbackCache = false;
        final long id = roleJson.getLong("id");
        if (guild == null)
            guild = (GuildImpl) getJDA().getGuildsView().get(guildId);
        RoleImpl role = (RoleImpl) guild.getRolesView().get(id);
        if (role == null) {
            SnowflakeCacheViewImpl<Role> roleView = guild.getRolesView();
            try (UnlockHook hook = roleView.writeLock()) {
                role = new RoleImpl(id, guild);
                playbackCache = roleView.getMap().put(id, role) == null;
            }
        }
        role.setName(roleJson.getString("name"));
        if (playbackCache)
            getJDA().getEventCache().playbackCache(EventCache.Type.ROLE, id);
        return role;
    }

    public Message createMessage(DataObject jsonObject, boolean modifyCache) {
        final long channelId = jsonObject.getLong("channel_id");

        MessageChannel chan = getJDA().getTextChannelById(channelId);
        if (chan == null)
            throw new IllegalArgumentException(MISSING_CHANNEL);

        return createMessage(jsonObject, chan, modifyCache);
    }

    public Message createMessage(DataObject jsonObject, MessageChannel chan, boolean modifyCache) {
        final long id = jsonObject.getLong("id");
        final DataObject author = jsonObject.getObject("author");
        final long authorId = author.getLong("id");
        Member member = null;

        if (chan.getType().isGuild() && !jsonObject.isNull("member") && modifyCache) {
            GuildChannel guildChannel = (GuildChannel) chan;
            Guild guild = guildChannel.getGuild();
            MemberImpl cachedMember = (MemberImpl) guild.getMemberById(authorId);
            // Update member cache with new information if needed
            if (cachedMember == null) {
                DataObject memberJson = jsonObject.getObject("member");
                memberJson.put("user", author);
                LOG.trace("Initializing member from message create {}", memberJson);
                member = createMember((GuildImpl) guild, memberJson);
            } else {
                member = cachedMember;
            }
        }

        final String content = jsonObject.getString("content", "");
        final boolean fromWebhook = jsonObject.hasKey("webhook_id");
        final boolean tts = jsonObject.getBoolean("tts");

        User user;
        switch (chan.getType()) {
            case GROUP:
                throw new IllegalStateException("Cannot build a message for a group channel, how did this even get here?");
            case TEXT:
                Guild guild = ((TextChannel) chan).getGuild();
                if (member == null)
                    member = guild.getMemberById(authorId);
                user = member != null ? member.getUser() : null;
                if (user == null) {
                    if (fromWebhook || !modifyCache)
                        user = createFakeUser(author, false);
                    else
                        throw new IllegalArgumentException(MISSING_USER);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid Channel for creating a Message [" + chan.getType() + ']');
        }

        if (modifyCache && !fromWebhook)
            updateUser((UserImpl) user, author);

        TLongSet mentionedRoles = new TLongHashSet();
        TLongSet mentionedUsers = new TLongHashSet(map(jsonObject, "mentions", (o) -> o.getLong("id")));
        Optional<DataArray> roleMentionArr = jsonObject.optArray("mention_roles");
        roleMentionArr.ifPresent((arr) ->
        {
            for (int i = 0; i < arr.length(); i++)
                mentionedRoles.add(arr.getLong(i));
        });

        MessageType type = MessageType.fromId(jsonObject.getInt("type"));
        Message message;
        if (type == MessageType.DEFAULT) {
            message = new Message(id, chan, type,
                    mentionedUsers, mentionedRoles,
                    content, user, member);
        } else {
            throw new IllegalArgumentException(UNKNOWN_MESSAGE_TYPE);
        }

        if (!message.isFromGuild())
            return message;

        GuildImpl guild = (GuildImpl) message.getGuild();

        if (guild.isLoaded())
            return message;

        List<User> mentionedUsersList = new ArrayList<>();
        List<Member> mentionedMembersList = new ArrayList<>();
        DataArray userMentions = jsonObject.getArray("mentions");

        for (int i = 0; i < userMentions.length(); i++) {
            DataObject mentionJson = userMentions.getObject(i);
            if (mentionJson.isNull("member")) {
                User mentionedUser = createFakeUser(mentionJson, false);
                mentionedUsersList.add(mentionedUser);
                Member mentionedMember = guild.getMember(mentionedUser);
                if (mentionedMember != null)
                    mentionedMembersList.add(mentionedMember);
                continue;
            }

            DataObject memberJson = mentionJson.getObject("member");
            mentionJson.remove("member");
            memberJson.put("user", mentionJson);
            Member mentionedMember = createMember(guild, memberJson);
            mentionedMembersList.add(mentionedMember);
            mentionedUsersList.add(mentionedMember.getUser());
        }

        if (!mentionedUsersList.isEmpty())
            message.setMentions(mentionedUsersList, mentionedMembersList);
        return message;
    }

    private <T> List<T> map(DataObject jsonObject, String key, Function<DataObject, T> convert) {
        if (jsonObject.isNull(key))
            return Collections.emptyList();

        final DataArray arr = jsonObject.getArray(key);
        final List<T> mappedObjects = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            DataObject obj = arr.getObject(i);
            T result = convert.apply(obj);
            if (result != null)
                mappedObjects.add(result);
        }

        return mappedObjects;
    }
}
