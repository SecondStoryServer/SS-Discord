package me.syari.ss.discord.internal.entities;

import gnu.trove.map.TLongObjectMap;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.MemberCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SortedSnowflakeCacheViewImpl;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GuildImpl implements Guild {
    private final long id;
    private final JDAImpl api;

    private final SortedSnowflakeCacheViewImpl<TextChannel> textChannelCache = new SortedSnowflakeCacheViewImpl<>(TextChannel.class, GuildChannel::getName, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<Role> roleCache = new SortedSnowflakeCacheViewImpl<>(Role.class, Role::getName, Comparator.reverseOrder());
    private final SnowflakeCacheViewImpl<Emote> emoteCache = new SnowflakeCacheViewImpl<>(Emote.class, Emote::getName);
    private final MemberCacheViewImpl memberCache = new MemberCacheViewImpl();

    // user -> channel -> override
    private final TLongObjectMap<TLongObjectMap<DataObject>> overrideMap = MiscUtil.newLongMap();

    private final CompletableFuture<Void> chunkingCallback = new CompletableFuture<>();

    private Member owner;
    private String name;
    private long ownerId;
    private Set<String> features;
    private Role publicRole;
    private final VerificationLevel verificationLevel = VerificationLevel.UNKNOWN;
    private boolean available;
    private int memberCount;

    public GuildImpl(JDAImpl api, long id) {
        this.id = id;
        this.api = api;
    }

    @Override
    public boolean isLoaded() {
        // Only works with guild subscriptions
        return getJDA().isGuildSubscriptions()
                && (long) getMemberCount() <= getMemberCache().size();
    }

    @Override
    public int getMemberCount() {
        return memberCount;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public Set<String> getFeatures() {
        return features;
    }

    @Nonnull
    @Override
    @Deprecated
    public RestAction<String> retrieveVanityUrl() {
        if (!getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            throw new InsufficientPermissionException(this, Permission.MANAGE_SERVER);
        if (!getFeatures().contains("VANITY_URL"))
            throw new IllegalStateException("This guild doesn't have a vanity url");

        Route.CompiledRoute route = Route.Guilds.GET_VANITY_URL.compile(getId());

        return new RestActionImpl<>(getJDA(), route,
                (response, request) -> response.getObject().getString("code"));
    }

    @Override
    public Member getOwner() {
        return owner;
    }

    @Override
    public long getOwnerIdLong() {
        return ownerId;
    }

    @Override
    public boolean isMember(@Nonnull User user) {
        return memberCache.get(user.getIdLong()) != null;
    }

    @Nonnull
    @Override
    public Member getSelfMember() {
        Member member = getMember(getJDA().getSelfUser());
        if (member == null)
            throw new IllegalStateException("Guild does not have a self member");
        return member;
    }

    @Override
    public Member getMember(@Nonnull User user) {
        Checks.notNull(user, "User");
        return getMemberById(user.getIdLong());
    }

    @Nonnull
    @Override
    public MemberCacheView getMemberCache() {
        return memberCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
        return textChannelCache;
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<Role> getRoleCache() {
        return roleCache;
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return emoteCache;
    }

    @Nonnull
    @Override
    public List<GuildChannel> getChannels(boolean includeHidden) {
        Member self = getSelfMember();
        Predicate<GuildChannel> filterHidden = it -> self.hasPermission(it, Permission.VIEW_CHANNEL);

        List<GuildChannel> channels;
        SnowflakeCacheViewImpl<TextChannel> textView = getTextChannelsView();
        List<TextChannel> textChannels;
        try (UnlockHook textHook = textView.readLock()) {
            if (includeHidden) {
                textChannels = textView.asList();
            } else {
                textChannels = textView.stream().filter(filterHidden).collect(Collectors.toList());
            }
            channels = new ArrayList<>(textChannels.size());
        }
        Collections.sort(channels);

        return Collections.unmodifiableList(channels);
    }

    @Nonnull
    @Override
    public Role getPublicRole() {
        return publicRole;
    }

    @Nonnull
    @Override
    public JDAImpl getJDA() {
        return api;
    }

    @Nonnull
    @Override
    public VerificationLevel getVerificationLevel() {
        return verificationLevel;
    }

    @Override
    public boolean checkVerification() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isAvailable() {
        return available;
    }

    @Override
    public long getIdLong() {
        return id;
    }

    // ---- Setters -----

    public GuildImpl setAvailable(boolean available) {
        this.available = available;
        return this;
    }

    public void setOwner(Member owner) {
        // Only cache owner if user cache is enabled
        if (getJDA().isGuildSubscriptions())
            this.owner = owner;
    }

    public GuildImpl setName(String name) {
        this.name = name;
        return this;
    }

    public GuildImpl setIconId(String iconId) {
        return this;
    }

    public void setFeatures(Set<String> features) {
        this.features = Collections.unmodifiableSet(features);
    }

    public void setPublicRole(Role publicRole) {
        this.publicRole = publicRole;
    }

    public GuildImpl setBoostTier(int tier) {
        BoostTier boostTier = BoostTier.fromKey(tier);
        return this;
    }

    public GuildImpl setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public void setMemberCount(int count) {
        this.memberCount = count;
    }

    // -- Map getters --

    public SortedSnowflakeCacheViewImpl<TextChannel> getTextChannelsView() {
        return textChannelCache;
    }

    public SortedSnowflakeCacheViewImpl<Role> getRolesView() {
        return roleCache;
    }

    public SnowflakeCacheViewImpl<Emote> getEmotesView() {
        return emoteCache;
    }

    public MemberCacheViewImpl getMembersView() {
        return memberCache;
    }

    // -- Member Tracking --

    public TLongObjectMap<DataObject> removeOverrideMap(long userId) {
        return overrideMap.remove(userId);
    }

    public void cacheOverride(long userId, long channelId, DataObject obj) {
        if (!getJDA().isGuildSubscriptions())
            return;
        EntityBuilder.LOG.debug("Caching permission override of unloaded member {}", obj);
        TLongObjectMap<DataObject> channelMap = overrideMap.get(userId);
        if (channelMap == null)
            overrideMap.put(userId, channelMap = MiscUtil.newLongMap());
        channelMap.put(channelId, obj);
    }

    public void acknowledgeMembers() {
        if (memberCache.size() == memberCount && !chunkingCallback.isDone()) {
            JDALogger.getLog(Guild.class).debug("Chunking completed for guild {}", this);
            chunkingCallback.complete(null);
        }
    }

    // -- Object overrides --

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GuildImpl))
            return false;
        GuildImpl oGuild = (GuildImpl) o;
        return this.id == oGuild.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "G:" + getName() + '(' + id + ')';
    }
}
