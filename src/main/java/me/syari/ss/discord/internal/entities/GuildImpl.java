package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.cache.MemberCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SortedSnowflakeCacheViewImpl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GuildImpl implements Guild {
    private final long id;
    private final JDAImpl api;

    private final SortedSnowflakeCacheViewImpl<TextChannel> textChannelCache = new SortedSnowflakeCacheViewImpl<>(TextChannel.class, GuildChannel::getName, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<Role> roleCache = new SortedSnowflakeCacheViewImpl<>(Role.class, Role::getName, Comparator.reverseOrder());
    private final SnowflakeCacheViewImpl<Emote> emoteCache = new SnowflakeCacheViewImpl<>(Emote.class, Emote::getName);
    private final MemberCacheViewImpl memberCache = new MemberCacheViewImpl();

    private final CompletableFuture<Void> chunkingCallback = new CompletableFuture<>();

    private Member owner;
    private String name;
    private long ownerId;
    private Set<String> features;
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

    public void setFeatures(Set<String> features) {
        this.features = Collections.unmodifiableSet(features);
    }

    public void setPublicRole(Role publicRole) {
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
