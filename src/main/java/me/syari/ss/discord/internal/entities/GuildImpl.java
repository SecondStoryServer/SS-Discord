

package me.syari.ss.discord.internal.entities;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.RoleAction;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.requests.restaction.RoleActionImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.MemberCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SortedSnowflakeCacheViewImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GuildImpl implements Guild
{
    private final long id;
    private final JDAImpl api;

    private final SortedSnowflakeCacheViewImpl<Category> categoryCache = new SortedSnowflakeCacheViewImpl<>(Category.class, GuildChannel::getName, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<VoiceChannel> voiceChannelCache = new SortedSnowflakeCacheViewImpl<>(VoiceChannel.class, GuildChannel::getName, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<StoreChannel> storeChannelCache = new SortedSnowflakeCacheViewImpl<>(StoreChannel.class, StoreChannel::getName, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<TextChannel> textChannelCache = new SortedSnowflakeCacheViewImpl<>(TextChannel.class, GuildChannel::getName, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<Role> roleCache = new SortedSnowflakeCacheViewImpl<>(Role.class, Role::getName, Comparator.reverseOrder());
    private final SnowflakeCacheViewImpl<Emote> emoteCache = new SnowflakeCacheViewImpl<>(Emote.class, Emote::getName);
    private final MemberCacheViewImpl memberCache = new MemberCacheViewImpl();

    // user -> channel -> override
    private final TLongObjectMap<TLongObjectMap<DataObject>> overrideMap = MiscUtil.newLongMap();

    private final CompletableFuture<Void> chunkingCallback = new CompletableFuture<>();

    private Member owner;
    private String name;
    private String iconId, splashId;
    private String region;
    private String vanityCode;
    private String description, banner;
    private int maxPresences, maxMembers;
    private int boostCount;
    private long ownerId;
    private Set<String> features;
    private VoiceChannel afkChannel;
    private TextChannel systemChannel;
    private Role publicRole;
    private VerificationLevel verificationLevel = VerificationLevel.UNKNOWN;
    private NotificationLevel defaultNotificationLevel = NotificationLevel.UNKNOWN;
    private MFALevel mfaLevel = MFALevel.UNKNOWN;
    private ExplicitContentLevel explicitContentLevel = ExplicitContentLevel.UNKNOWN;
    private Timeout afkTimeout;
    private BoostTier boostTier = BoostTier.NONE;
    private boolean available;
    private boolean canSendVerification = false;
    private int memberCount;

    public GuildImpl(JDAImpl api, long id)
    {
        this.id = id;
        this.api = api;
    }

    @Override
    public boolean isLoaded()
    {
        // Only works with guild subscriptions
        return getJDA().isGuildSubscriptions()
                && (long) getMemberCount() <= getMemberCache().size();
    }

    @Override
    public int getMemberCount()
    {
        return memberCount;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getIconId()
    {
        return iconId;
    }

    @Nonnull
    @Override
    public Set<String> getFeatures()
    {
        return features;
    }

    @Override
    public String getSplashId()
    {
        return splashId;
    }

    @Nonnull
    @Override
    @Deprecated
    public RestAction<String> retrieveVanityUrl()
    {
        if (!getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            throw new InsufficientPermissionException(this, Permission.MANAGE_SERVER);
        if (!getFeatures().contains("VANITY_URL"))
            throw new IllegalStateException("This guild doesn't have a vanity url");

        Route.CompiledRoute route = Route.Guilds.GET_VANITY_URL.compile(getId());

        return new RestActionImpl<>(getJDA(), route,
            (response, request) -> response.getObject().getString("code"));
    }

    @Nullable
    @Override
    public String getVanityCode()
    {
        return vanityCode;
    }

    @Nullable
    @Override
    public String getDescription()
    {
        return description;
    }

    @Nullable
    @Override
    public String getBannerId()
    {
        return banner;
    }

    @Nonnull
    @Override
    public BoostTier getBoostTier()
    {
        return boostTier;
    }

    @Override
    public int getBoostCount()
    {
        return boostCount;
    }

    @Override
    public int getMaxMembers()
    {
        return maxMembers;
    }

    @Override
    public int getMaxPresences()
    {
        return maxPresences;
    }

    @Override
    public VoiceChannel getAfkChannel()
    {
        return afkChannel;
    }

    @Override
    public TextChannel getSystemChannel()
    {
        return systemChannel;
    }

    @Override
    public Member getOwner()
    {
        return owner;
    }

    @Override
    public long getOwnerIdLong()
    {
        return ownerId;
    }

    @Nonnull
    @Override
    public Timeout getAfkTimeout()
    {
        return afkTimeout;
    }

    @Nonnull
    @Override
    public String getRegionRaw()
    {
        return region;
    }

    @Override
    public boolean isMember(@Nonnull User user)
    {
        return memberCache.get(user.getIdLong()) != null;
    }

    @Nonnull
    @Override
    public Member getSelfMember()
    {
        Member member = getMember(getJDA().getSelfUser());
        if (member == null)
            throw new IllegalStateException("Guild does not have a self member");
        return member;
    }

    @Override
    public Member getMember(@Nonnull User user)
    {
        Checks.notNull(user, "User");
        return getMemberById(user.getIdLong());
    }

    @Nonnull
    @Override
    public MemberCacheView getMemberCache()
    {
        return memberCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<Category> getCategoryCache()
    {
        return categoryCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache()
    {
        return storeChannelCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<TextChannel> getTextChannelCache()
    {
        return textChannelCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache()
    {
        return voiceChannelCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<Role> getRoleCache()
    {
        return roleCache;
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<Emote> getEmoteCache()
    {
        return emoteCache;
    }

    @Nonnull
    @Override
    public List<GuildChannel> getChannels(boolean includeHidden)
    {
        Member self = getSelfMember();
        Predicate<GuildChannel> filterHidden = it -> self.hasPermission(it, Permission.VIEW_CHANNEL);

        List<GuildChannel> channels;
        SnowflakeCacheViewImpl<Category> categoryView = getCategoriesView();
        SnowflakeCacheViewImpl<VoiceChannel> voiceView = getVoiceChannelsView();
        SnowflakeCacheViewImpl<TextChannel> textView = getTextChannelsView();
        SnowflakeCacheViewImpl<StoreChannel> storeView = getStoreChannelView();
        List<TextChannel> textChannels;
        List<StoreChannel> storeChannels;
        List<VoiceChannel> voiceChannels;
        List<Category> categories;
        try (UnlockHook categoryHook = categoryView.readLock();
             UnlockHook voiceHook = voiceView.readLock();
             UnlockHook textHook = textView.readLock();
             UnlockHook storeHook = storeView.readLock())
        {
            if (includeHidden)
            {
                storeChannels = storeView.asList();
                textChannels = textView.asList();
                voiceChannels = voiceView.asList();
            }
            else
            {
                storeChannels = storeView.stream().filter(filterHidden).collect(Collectors.toList());
                textChannels = textView.stream().filter(filterHidden).collect(Collectors.toList());
                voiceChannels = voiceView.stream().filter(filterHidden).collect(Collectors.toList());
            }
            categories = categoryView.asList(); // we filter categories out when they are empty (no visible channels inside)
            channels = new ArrayList<>((int) categoryView.size() + voiceChannels.size() + textChannels.size() + storeChannels.size());
        }

        storeChannels.stream().filter(it -> it.getParent() == null).forEach(channels::add);
        textChannels.stream().filter(it -> it.getParent() == null).forEach(channels::add);
        Collections.sort(channels);
        voiceChannels.stream().filter(it -> it.getParent() == null).forEach(channels::add);

        for (Category category : categories)
        {
            List<GuildChannel> children;
            if (includeHidden)
            {
                children = category.getChannels();
            }
            else
            {
                children = category.getChannels().stream().filter(filterHidden).collect(Collectors.toList());
                if (children.isEmpty())
                    continue;
            }

            channels.add(category);
            channels.addAll(children);
        }

        return Collections.unmodifiableList(channels);
    }

    @Nonnull
    @Override
    public Role getPublicRole()
    {
        return publicRole;
    }

    @Nonnull
    @Override
    public JDAImpl getJDA()
    {
        return api;
    }

    @Nonnull
    @Override
    public VerificationLevel getVerificationLevel()
    {
        return verificationLevel;
    }

    @Nonnull
    @Override
    public NotificationLevel getDefaultNotificationLevel()
    {
        return defaultNotificationLevel;
    }

    @Nonnull
    @Override
    public MFALevel getRequiredMFALevel()
    {
        return mfaLevel;
    }

    @Nonnull
    @Override
    public ExplicitContentLevel getExplicitContentLevel()
    {
        return explicitContentLevel;
    }

    @Override
    public boolean checkVerification()
    {
        if (getJDA().getAccountType() == AccountType.BOT)
            return true;
        if(canSendVerification)
            return true;

        if (getJDA().getSelfUser().getPhoneNumber() != null)
            return canSendVerification = true;

        switch (verificationLevel)
        {
            case VERY_HIGH:
                break; // we already checked for a verified phone number
            case HIGH:
                if (ChronoUnit.MINUTES.between(getSelfMember().getTimeJoined(), OffsetDateTime.now()) < 10)
                    break;
            case MEDIUM:
                if (ChronoUnit.MINUTES.between(getJDA().getSelfUser().getTimeCreated(), OffsetDateTime.now()) < 5)
                    break;
            case LOW:
                if (!getJDA().getSelfUser().isVerified())
                    break;
            case NONE:
                canSendVerification = true;
                return true;
            case UNKNOWN:
                return true; // try and let discord decide
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean isAvailable()
    {
        return available;
    }

    @Override
    public long getIdLong()
    {
        return id;
    }

    @Nonnull
    @Override
    public RoleAction createRole()
    {
        checkPermission();
        return new RoleActionImpl(this);
    }

    protected void checkPermission()
    {
        if (!getSelfMember().hasPermission(Permission.MANAGE_ROLES))
            throw new InsufficientPermissionException(this, Permission.MANAGE_ROLES);
    }

    // ---- Setters -----

    public GuildImpl setAvailable(boolean available)
    {
        this.available = available;
        return this;
    }

    public GuildImpl setOwner(Member owner)
    {
        // Only cache owner if user cache is enabled
        if (getJDA().isGuildSubscriptions())
            this.owner = owner;
        return this;
    }

    public GuildImpl setName(String name)
    {
        this.name = name;
        return this;
    }

    public GuildImpl setIconId(String iconId)
    {
        this.iconId = iconId;
        return this;
    }

    public GuildImpl setFeatures(Set<String> features)
    {
        this.features = Collections.unmodifiableSet(features);
        return this;
    }

    public GuildImpl setSplashId(String splashId)
    {
        this.splashId = splashId;
        return this;
    }

    public GuildImpl setRegion(String region)
    {
        this.region = region;
        return this;
    }

    public GuildImpl setVanityCode(String code)
    {
        this.vanityCode = code;
        return this;
    }

    public GuildImpl setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public GuildImpl setBannerId(String bannerId)
    {
        this.banner = bannerId;
        return this;
    }

    public GuildImpl setMaxPresences(int maxPresences)
    {
        this.maxPresences = maxPresences;
        return this;
    }

    public GuildImpl setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
        return this;
    }

    public GuildImpl setAfkChannel(VoiceChannel afkChannel)
    {
        this.afkChannel = afkChannel;
        return this;
    }

    public GuildImpl setSystemChannel(TextChannel systemChannel)
    {
        this.systemChannel = systemChannel;
        return this;
    }

    public GuildImpl setPublicRole(Role publicRole)
    {
        this.publicRole = publicRole;
        return this;
    }

    public GuildImpl setVerificationLevel(VerificationLevel level)
    {
        this.verificationLevel = level;
        this.canSendVerification = false;   //recalc on next send
        return this;
    }

    public GuildImpl setDefaultNotificationLevel(NotificationLevel level)
    {
        this.defaultNotificationLevel = level;
        return this;
    }

    public GuildImpl setRequiredMFALevel(MFALevel level)
    {
        this.mfaLevel = level;
        return this;
    }

    public GuildImpl setExplicitContentLevel(ExplicitContentLevel level)
    {
        this.explicitContentLevel = level;
        return this;
    }

    public GuildImpl setAfkTimeout(Timeout afkTimeout)
    {
        this.afkTimeout = afkTimeout;
        return this;
    }

    public GuildImpl setBoostTier(int tier)
    {
        this.boostTier = BoostTier.fromKey(tier);
        return this;
    }

    public GuildImpl setBoostCount(int count)
    {
        this.boostCount = count;
        return this;
    }

    public GuildImpl setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
        return this;
    }

    public GuildImpl setMemberCount(int count)
    {
        this.memberCount = count;
        return this;
    }

    // -- Map getters --

    public SortedSnowflakeCacheViewImpl<Category> getCategoriesView()
    {
        return categoryCache;
    }

    public SortedSnowflakeCacheViewImpl<StoreChannel> getStoreChannelView()
    {
        return storeChannelCache;
    }

    public SortedSnowflakeCacheViewImpl<TextChannel> getTextChannelsView()
    {
        return textChannelCache;
    }

    public SortedSnowflakeCacheViewImpl<VoiceChannel> getVoiceChannelsView()
    {
        return voiceChannelCache;
    }

    public SortedSnowflakeCacheViewImpl<Role> getRolesView()
    {
        return roleCache;
    }

    public SnowflakeCacheViewImpl<Emote> getEmotesView()
    {
        return emoteCache;
    }

    public MemberCacheViewImpl getMembersView()
    {
        return memberCache;
    }

    // -- Member Tracking --

    public TLongObjectMap<DataObject> removeOverrideMap(long userId)
    {
        return overrideMap.remove(userId);
    }

    public void pruneChannelOverrides(long channelId)
    {
        WebSocketClient.LOG.debug("Pruning cached overrides for channel with id {}", channelId);
        overrideMap.retainEntries((key, value) -> {
            DataObject removed = value.remove(channelId);
            return !value.isEmpty();
        });
    }

    public void cacheOverride(long userId, long channelId, DataObject obj)
    {
        if (!getJDA().isGuildSubscriptions())
            return;
        EntityBuilder.LOG.debug("Caching permission override of unloaded member {}", obj);
        TLongObjectMap<DataObject> channelMap = overrideMap.get(userId);
        if (channelMap == null)
            overrideMap.put(userId, channelMap = MiscUtil.newLongMap());
        channelMap.put(channelId, obj);
    }

    public void updateCachedOverrides(AbstractChannelImpl<?, ?> channel, DataArray newOverrides)
    {
        if (!getJDA().isGuildSubscriptions())
            return;
        long channelId = channel.getIdLong();
        // extract user ids
        TLongSet users = new TLongHashSet();
        for (int i = 0; i < newOverrides.length(); i++)
        {
            DataObject obj = newOverrides.getObject(i);
            if (!obj.getString("type", "").equals("member"))
                continue;
            long id = obj.getUnsignedLong("id");
            // remember that this user has an override
            users.add(id);
        }

        // now remove the overrides that are missing
        TLongSet toRemove = new TLongHashSet();
        overrideMap.forEachEntry((userId, overrides) ->
        {
            if (users.contains(userId))
                return true;
            // remove for the channel
            overrides.remove(channelId);
            // remember to remove this map if its empty now
            if (overrides.isEmpty())
                toRemove.add(userId);
            return true;
        });
        // remove all empty maps
        overrideMap.keySet().removeAll(toRemove);
    }

    public void onMemberAdd()
    {
        memberCount++;
    }

    public void onMemberRemove()
    {
        memberCount--;
        acknowledgeMembers();
    }

    public void acknowledgeMembers()
    {
        if (memberCache.size() == memberCount && !chunkingCallback.isDone())
        {
            JDALogger.getLog(Guild.class).debug("Chunking completed for guild {}", this);
            chunkingCallback.complete(null);
        }
    }

    // -- Object overrides --

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof GuildImpl))
            return false;
        GuildImpl oGuild = (GuildImpl) o;
        return this.id == oGuild.id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public String toString()
    {
        return "G:" + getName() + '(' + id + ')';
    }
}
