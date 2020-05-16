package me.syari.ss.discord.internal.entities;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EntityBuilder {
    public static final Logger LOG = JDALogger.getLog(EntityBuilder.class);
    public static final String MISSING_CHANNEL = "MISSING_CHANNEL";
    public static final String MISSING_USER = "MISSING_USER";
    public static final String UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE";
    private static final Set<String> richGameFields;

    static {
        Set<String> tmp = new HashSet<>();
        tmp.add("application_id");
        tmp.add("assets");
        tmp.add("details");
        tmp.add("flags");
        tmp.add("party");
        tmp.add("session_id");
        tmp.add("state");
        tmp.add("sync_id");
        richGameFields = Collections.unmodifiableSet(tmp);
    }

    protected final JDAImpl api;

    public EntityBuilder(JDA api) {
        this.api = (JDAImpl) api;
    }

    public JDAImpl getJDA() {
        return api;
    }

    public void createSelfUser(DataObject self) {
        SelfUserImpl selfUser = (SelfUserImpl) (getJDA().hasSelfUser() ? getJDA().getSelfUser() : null);
        if (selfUser == null) {
            final long id = self.getLong("id");
            selfUser = new SelfUserImpl(id, getJDA());
            getJDA().setSelfUser(selfUser);
        }

        SnowflakeCacheViewImpl<User> userView = getJDA().getUsersView();
        try (UnlockHook hook = userView.writeLock()) {
            if (userView.getElementById(selfUser.getIdLong()) == null)
                userView.getMap().put(selfUser.getIdLong(), selfUser);
        }

        selfUser.setName(self.getString("username"))
                .setDiscriminator(self.getString("discriminator"))
                .setAvatarId(self.getString("avatar", null))
                .setBot(self.getBoolean("bot"));
    }

    private void createGuildEmotePass(GuildImpl guildObj, DataArray array) {
        if (!getJDA().isCacheFlagSet(CacheFlag.EMOTE))
            return;
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
                emoteMap.put(emoteId, createEmote(guildObj, object, false));
            }
        }
    }

    public GuildImpl createGuild(long guildId, DataObject guildJson, TLongObjectMap<DataObject> members, int memberCount) {
        final GuildImpl guildObj = new GuildImpl(getJDA(), guildId);
        final String name = guildJson.getString("name", "");
        final String iconId = guildJson.getString("icon", null);
        final DataArray roleArray = guildJson.getArray("roles");
        final DataArray channelArray = guildJson.getArray("channels");
        final DataArray emotesArray = guildJson.getArray("emojis");
        final DataArray voiceStateArray = guildJson.getArray("voice_states");
        final Optional<DataArray> featuresArray = guildJson.optArray("features");
        final Optional<DataArray> presencesArray = guildJson.optArray("presences");
        final long ownerId = guildJson.getUnsignedLong("owner_id", 0L);
        final int boostTier = guildJson.getInt("premium_tier", 0);

        guildObj.setAvailable(true)
                .setName(name)
                .setIconId(iconId)
                .setOwnerId(ownerId)
                .setBoostTier(boostTier)
                .setMemberCount(memberCount);

        SnowflakeCacheViewImpl<Guild> guildView = getJDA().getGuildsView();
        try (UnlockHook hook = guildView.writeLock()) {
            guildView.getMap().put(guildId, guildObj);
        }

        guildObj.setFeatures(featuresArray.map(it ->
                StreamSupport.stream(it.spliterator(), false)
                        .map(String::valueOf)
                        .collect(Collectors.toSet())
        ).orElse(Collections.emptySet()));

        SnowflakeCacheViewImpl<Role> roleView = guildObj.getRolesView();
        try (UnlockHook hook = roleView.writeLock()) {
            TLongObjectMap<Role> map = roleView.getMap();
            for (int i = 0; i < roleArray.length(); i++) {
                DataObject obj = roleArray.getObject(i);
                Role role = createRole(guildObj, obj, guildId);
                map.put(role.getIdLong(), role);
                if (role.getIdLong() == guildObj.getIdLong())
                    guildObj.setPublicRole(role);
            }
        }

        try (UnlockHook h1 = guildObj.getMembersView().writeLock();
             UnlockHook h2 = getJDA().getUsersView().writeLock()) {
            //Add members to cache when subscriptions are disabled when they appear here
            // this is done because we can still keep track of members in voice channels
            TLongObjectMap<Member> memberCache = guildObj.getMembersView().getMap();
            TLongObjectMap<User> userCache = getJDA().getUsersView().getMap();
            for (DataObject memberJson : members.valueCollection()) {
                MemberImpl member = createMember(guildObj, memberJson);
                // ignore members in voice channels if voice state cache is disabled
                if (member.getUser().equals(getJDA().getSelfUser()) || getJDA().isCacheFlagSet(CacheFlag.VOICE_STATE)) {
                    memberCache.put(member.getIdLong(), member);
                    userCache.put(member.getIdLong(), member.getUser());
                }
            }
        }

        if (guildObj.getOwner() == null)
            LOG.debug("Finished setup for guild with a null owner. GuildId: {} OwnerId: {}", guildId, guildJson.opt("owner_id").orElse(null));

        for (int i = 0; i < channelArray.length(); i++) {
            DataObject channelJson = channelArray.getObject(i);
            createGuildChannel(guildObj, channelJson);
        }

        createGuildEmotePass(guildObj, emotesArray);
        createGuildVoiceStatePass(guildObj, voiceStateArray);

        presencesArray.ifPresent((arr) -> {
            for (int i = 0; i < arr.length(); i++) {
                DataObject presence = arr.getObject(i);
                final long userId = presence.getObject("user").getLong("id");
                MemberImpl member = (MemberImpl) guildObj.getMembersView().get(userId);

                if (member == null)
                    LOG.debug("Received a ghost presence in GuildFirstPass! UserId: {} Guild: {}", userId, guildObj);
                else
                    createPresence(member, presence);
            }
        });

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

    public void createGuildVoiceStatePass(GuildImpl guildObj, DataArray voiceStates) {
        for (int i = 0; i < voiceStates.length(); i++) {
            DataObject voiceStateJson = voiceStates.getObject(i);
            final long userId = voiceStateJson.getLong("user_id");
            Member member = guildObj.getMembersView().get(userId);
            if (member == null) {
                if (getJDA().isCacheFlagSet(CacheFlag.VOICE_STATE)) {
                    LOG.error("Received a VoiceState for a unknown Member! GuildId: "
                            + guildObj.getId() + " MemberId: " + voiceStateJson.getString("user_id"));
                }
            }
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
                    .setAvatarId(user.getString("avatar", null))
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
        String oldAvatar = userObj.getAvatarId();
        String newAvatar = user.getString("avatar", null);

        if (!oldName.equals(newName)) {
            userObj.setName(newName);
        }

        if (!oldDiscriminator.equals(newDiscriminator)) {
            userObj.setDiscriminator(newDiscriminator);
        }

        if (!Objects.equals(oldAvatar, newAvatar)) {
            userObj.setAvatarId(newAvatar);
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
                // Cache member if guild subscriptions are enabled or the user is the self user
                if (getJDA().isGuildSubscriptions() || user.equals(getJDA().getSelfUser())) {
                    playbackCache = memberView.getMap().put(user.getIdLong(), member) == null;
                    // load the overrides
                    TLongObjectMap<DataObject> cachedOverrides = guild.removeOverrideMap(user.getIdLong());
                    if (cachedOverrides != null) {
                        cachedOverrides.forEachEntry((channelId, override) ->
                        {
                            GuildChannel channel = guild.getGuildChannelById(channelId);
                            if (channel instanceof AbstractChannelImpl) // essentially a null check plus cast safety
                                createPermissionOverride(override, (AbstractChannelImpl) channel);
                            return true;
                        });
                    }
                } else // otherwise re-create every time!
                {
                    playbackCache = true;
                }
            }
            if (playbackCache && guild.getOwnerIdLong() == user.getIdLong()) {
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

        //In some contexts this is missing (PRESENCE_UPDATE and GUILD_MEMBER_UPDATE)
        // we call this incomplete and load the joined_at later through a MESSAGE_CREATE (if we get one)
        String joinedAtRaw = memberJson.opt("joined_at").map(String::valueOf).orElseGet(() -> guild.getTimeCreated().toString());
        TemporalAccessor joinedAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(joinedAtRaw);
        member.setJoinDate(Instant.from(joinedAt).toEpochMilli())
                .setNickname(memberJson.getString("nick", null));

        DataArray rolesJson = memberJson.getArray("roles");
        for (int k = 0; k < rolesJson.length(); k++) {
            final long roleId = rolesJson.getLong(k);
            Role r = guild.getRolesView().get(roleId);
            if (r == null) {
                LOG.debug("Received a Member with an unknown Role. MemberId: {} GuildId: {} roleId: {}",
                        member.getUser().getId(), guild.getId(), roleId);
            } else {
                member.getRoleSet().add(r);
            }
        }
    }

    public void updateMember(MemberImpl member, DataObject content, List<Role> newRoles) {
        //If newRoles is null that means that we didn't find a role that was in the array and was cached this event
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

    public void createPresence(MemberImpl member, DataObject presenceJson) {
        if (member == null)
            throw new NullPointerException("Provided member was null!");
        boolean cacheStatus = getJDA().isCacheFlagSet(CacheFlag.CLIENT_STATUS);

        DataObject clientStatusJson = !cacheStatus || presenceJson.isNull("client_status") ? null : presenceJson.getObject("client_status");
        if (clientStatusJson != null) {
            for (String key : clientStatusJson.keys()) {
                ClientType type = ClientType.fromKey(key);
                OnlineStatus status = OnlineStatus.fromKey(clientStatusJson.getString(key));
                member.setOnlineStatus(type, status);
            }
        }
    }

    public EmoteImpl createEmote(GuildImpl guildObj, DataObject json, boolean fake) {
        DataArray emoteRoles = json.optArray("roles").orElseGet(DataArray::empty);
        final long emoteId = json.getLong("id");
        final User user = json.isNull("user") ? null : createFakeUser(json.getObject("user"), false);
        EmoteImpl emoteObj = (EmoteImpl) guildObj.getEmoteById(emoteId);
        if (emoteObj == null)
            emoteObj = new EmoteImpl(emoteId, guildObj, fake);
        Set<Role> roleSet = emoteObj.getRoleSet();

        roleSet.clear();
        for (int j = 0; j < emoteRoles.length(); j++) {
            Role role = guildObj.getRoleById(emoteRoles.getString(j));
            if (role != null)
                roleSet.add(role);
        }
        if (user != null)
            emoteObj.setUser(user);
        return emoteObj
                .setName(json.getString("name", ""))
                .setAnimated(json.getBoolean("animated"))
                .setManaged(json.getBoolean("managed"));
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

        if (!json.isNull("permission_overwrites")) {
            DataArray overrides = json.getArray("permission_overwrites");
            createOverridesPass(channel, overrides);
        }

        channel.setParent(json.getLong("parent_id", 0))
                .setName(json.getString("name"))
                .setPosition(json.getInt("position"));
        if (playbackCache)
            getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
    }

    public void createOverridesPass(AbstractChannelImpl<?, ?> channel, DataArray overrides) {
        for (int i = 0; i < overrides.length(); i++) {
            try {
                createPermissionOverride(overrides.getObject(i), channel);
            } catch (NoSuchElementException e) {
                //Caused by Discord not properly clearing PermissionOverrides when a Member leaves a Guild.
                LOG.debug("{}. Ignoring PermissionOverride.", e.getMessage());
            } catch (IllegalArgumentException e) {
                //Missing handling for a type
                LOG.warn("{}. Ignoring PermissionOverride.", e.getMessage());
            }
        }
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
        role.setName(roleJson.getString("name"))
                .setRawPosition(roleJson.getInt("position"))
                .setRawPermissions(roleJson.getLong("permissions"))
                .setManaged(roleJson.getBoolean("managed"));
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
            if (cachedMember == null || cachedMember.isIncomplete() || !getJDA().isGuildSubscriptions()) {
                DataObject memberJson = jsonObject.getObject("member");
                memberJson.put("user", author);
                if (cachedMember == null)
                    LOG.trace("Initializing member from message create {}", memberJson);
                member = createMember((GuildImpl) guild, memberJson);
            } else {
                member = cachedMember;
            }
        }

        final String content = jsonObject.getString("content", "");
        final boolean fromWebhook = jsonObject.hasKey("webhook_id");
        final boolean tts = jsonObject.getBoolean("tts");
        final boolean mentionsEveryone = jsonObject.getBoolean("mention_everyone");

        final List<MessageEmbed> embeds = map(jsonObject, "embeds", this::createMessageEmbed);

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
                        throw new IllegalArgumentException(MISSING_USER); // Specifically for MESSAGE_CREATE
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid Channel for creating a Message [" + chan.getType() + ']');
        }

        if (modifyCache && !fromWebhook) // update the user information on message receive
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
            message = new Message(id, chan, type, fromWebhook,
                    mentionsEveryone, mentionedUsers, mentionedRoles, tts,
                    content, user, member, embeds);
        } else {
            throw new IllegalArgumentException(UNKNOWN_MESSAGE_TYPE);
        }

        if (!message.isFromGuild())
            return message;

        GuildImpl guild = (GuildImpl) message.getGuild();

        // Don't do more computations when members are loaded already
        if (guild.isLoaded())
            return message;

        // Load users/members from message object through mentions
        List<User> mentionedUsersList = new ArrayList<>();
        List<Member> mentionedMembersList = new ArrayList<>();
        DataArray userMentions = jsonObject.getArray("mentions");

        for (int i = 0; i < userMentions.length(); i++) {
            DataObject mentionJson = userMentions.getObject(i);
            if (mentionJson.isNull("member")) {
                // Can't load user without member context so fake them if possible
                User mentionedUser = createFakeUser(mentionJson, false);
                mentionedUsersList.add(mentionedUser);
                Member mentionedMember = guild.getMember(mentionedUser);
                if (mentionedMember != null)
                    mentionedMembersList.add(mentionedMember);
                continue;
            }

            // Load member/user from mention (gateway messages only)
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

    public MessageEmbed createMessageEmbed(DataObject content) {
        if (content.isNull("type"))
            throw new IllegalStateException("Encountered embed object with missing/null type field for Json: " + content);
        EmbedType type = EmbedType.fromKey(content.getString("type"));
        final String url = content.getString("url", null);
        final String title = content.getString("title", null);
        final String description = content.getString("description", null);
        final OffsetDateTime timestamp = content.isNull("timestamp") ? null : OffsetDateTime.parse(content.getString("timestamp"));
        final int color = content.isNull("color") ? Role.DEFAULT_COLOR_RAW : content.getInt("color");

        final MessageEmbed.Thumbnail thumbnail;
        if (content.isNull("thumbnail")) {
            thumbnail = null;
        } else {
            DataObject obj = content.getObject("thumbnail");
            thumbnail = new MessageEmbed.Thumbnail(obj.getString("url", null),
                    obj.getString("proxy_url", null),
                    obj.getInt("width", -1),
                    obj.getInt("height", -1));
        }

        final MessageEmbed.Provider provider;
        if (content.isNull("provider")) {
            provider = null;
        } else {
            DataObject obj = content.getObject("provider");
            provider = new MessageEmbed.Provider(obj.getString("name", null),
                    obj.getString("url", null));
        }

        final MessageEmbed.AuthorInfo author;
        if (content.isNull("author")) {
            author = null;
        } else {
            DataObject obj = content.getObject("author");
            author = new MessageEmbed.AuthorInfo(obj.getString("name", null),
                    obj.getString("url", null),
                    obj.getString("icon_url", null),
                    obj.getString("proxy_icon_url", null));
        }

        final MessageEmbed.VideoInfo video;
        if (content.isNull("video")) {
            video = null;
        } else {
            DataObject obj = content.getObject("video");
            video = new MessageEmbed.VideoInfo(obj.getString("url", null),
                    obj.getInt("width", -1),
                    obj.getInt("height", -1));
        }

        final MessageEmbed.Footer footer;
        if (content.isNull("footer")) {
            footer = null;
        } else {
            DataObject obj = content.getObject("footer");
            footer = new MessageEmbed.Footer(obj.getString("text", null),
                    obj.getString("icon_url", null),
                    obj.getString("proxy_icon_url", null));
        }

        final MessageEmbed.ImageInfo image;
        if (content.isNull("image")) {
            image = null;
        } else {
            DataObject obj = content.getObject("image");
            image = new MessageEmbed.ImageInfo(obj.getString("url", null),
                    obj.getString("proxy_url", null),
                    obj.getInt("width", -1),
                    obj.getInt("height", -1));
        }

        final List<MessageEmbed.Field> fields = map(content, "fields", (obj) ->
                new MessageEmbed.Field(obj.getString("name", null),
                        obj.getString("value", null),
                        obj.getBoolean("inline"),
                        false)
        );

        return createMessageEmbed(url, title, description, type, timestamp,
                color, thumbnail, provider, author, video, footer, image, fields);
    }

    public static MessageEmbed createMessageEmbed(String url, String title, String description, EmbedType type, OffsetDateTime timestamp,
                                                  int color, MessageEmbed.Thumbnail thumbnail, MessageEmbed.Provider siteProvider, MessageEmbed.AuthorInfo author,
                                                  MessageEmbed.VideoInfo videoInfo, MessageEmbed.Footer footer, MessageEmbed.ImageInfo image, List<MessageEmbed.Field> fields) {
        return new MessageEmbed(url, title, description, type, timestamp,
                color, thumbnail, siteProvider, author, videoInfo, footer, image, fields);
    }

    public void createPermissionOverride(DataObject override, AbstractChannelImpl<?, ?> chan) {
        IPermissionHolder permHolder;
        final long id = override.getLong("id");

        //Throwing NoSuchElementException for common issues with overrides that are not cleared properly by discord
        // when a member leaves or a role is deleted
        switch (override.getString("type")) {
            case "member":
                permHolder = chan.getGuild().getMemberById(id);
                if (permHolder == null) {
                    // cache override for later
                    chan.getGuild().cacheOverride(id, chan.getIdLong(), override);
                    return;
                }
                break;
            case "role":
                permHolder = chan.getGuild().getRolesView().get(id);
                if (permHolder == null)
                    throw new NoSuchElementException("Attempted to create a PermissionOverride for a non-existent role! JSON: " + override);
                break;
            default:
                throw new IllegalArgumentException("Provided with an unknown PermissionOverride type! JSON: " + override);
        }

        long allow = override.getLong("allow");
        long deny = override.getLong("deny");

        PermissionOverrideImpl permOverride = (PermissionOverrideImpl) chan.getPermissionOverride(permHolder);
        if (permOverride == null) {
            permOverride = new PermissionOverrideImpl(chan, permHolder);
            chan.getOverrideMap().put(permHolder.getIdLong(), permOverride);
        }
        permOverride.setAllow(allow).setDeny(deny);
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
