package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.cache.MemberCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SortedSnowflakeCacheViewImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class Guild implements ISnowflake {
    private final long id;
    private final JDAImpl api;

    private final SortedSnowflakeCacheViewImpl<TextChannel> textChannelCache = new SortedSnowflakeCacheViewImpl<>(TextChannel.class, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<Role> roleCache = new SortedSnowflakeCacheViewImpl<>(Role.class, Comparator.reverseOrder());
    private final SnowflakeCacheViewImpl<Emote> emoteCache = new SnowflakeCacheViewImpl<>(Emote.class);
    private final MemberCacheViewImpl memberCache = new MemberCacheViewImpl();

    private final CompletableFuture<Void> chunkingCallback = new CompletableFuture<>();

    private Member owner;
    private String name;
    private long ownerId;
    private int memberCount;

    public Guild(JDAImpl api, long id) {
        this.id = id;
        this.api = api;
    }

    public boolean isLoaded() {
        return getJDA().isGuildSubscriptions() && (long) getMemberCount() <= getMemberCache().size();
    }

    private int getMemberCount() {
        return memberCount;
    }

    @Nonnull
    private String getName() {
        return name;
    }

    public Member getOwner() {
        return owner;
    }

    public long getOwnerIdLong() {
        return ownerId;
    }

    public boolean isMember(@Nonnull User user) {
        return memberCache.get(user.getIdLong()) != null;
    }

    public Member getMember(@Nonnull User user) {
        Checks.notNull(user, "User");
        return getMemberById(user.getIdLong());
    }

    @Nonnull
    public MemberCacheView getMemberCache() {
        return memberCache;
    }

    @Nonnull
    public SortedSnowflakeCacheView<Role> getRoleCache() {
        return roleCache;
    }

    @Nonnull
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return emoteCache;
    }

    @Nonnull
    public JDAImpl getJDA() {
        return api;
    }

    @Override
    public long getIdLong() {
        return id;
    }


    @Nullable
    public Member getMemberById(long userId) {
        return getMemberCache().getElementById(userId);
    }

    @Nullable
    public Role getRoleById(@Nonnull String id) {
        return getRoleCache().getElementById(id);
    }

    @Nullable
    public Role getRoleById(long id) {
        return getRoleCache().getElementById(id);
    }

    @Nullable
    public Emote getEmoteById(long id) {
        return getEmoteCache().getElementById(id);
    }

    // ---- Setters -----

    public void setOwner(Member owner) {
        if (getJDA().isGuildSubscriptions())
            this.owner = owner;
    }

    public Guild setName(String name) {
        this.name = name;
        return this;
    }


    public Guild setOwnerId(long ownerId) {
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
        if (!(o instanceof Guild))
            return false;
        Guild oGuild = (Guild) o;
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
