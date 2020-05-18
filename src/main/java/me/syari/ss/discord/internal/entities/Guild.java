package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.cache.MemberCacheView;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SortedSnowflakeCacheViewImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class Guild implements ISnowflake {
    private final long id;
    private final JDAImpl api;

    private final SortedSnowflakeCacheViewImpl<TextChannel> textChannelCache = new SortedSnowflakeCacheViewImpl<>(TextChannel.class, Comparator.naturalOrder());
    private final SortedSnowflakeCacheViewImpl<Role> roleCache = new SortedSnowflakeCacheViewImpl<>(Role.class, Comparator.reverseOrder());
    private final SnowflakeCacheViewImpl<Emote> emoteCache = new SnowflakeCacheViewImpl<>(Emote.class);
    private final MemberCacheView memberCache = new MemberCacheView();

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
        return (long) getMemberCount() <= getMemberCache().size();
    }

    private int getMemberCount() {
        return memberCount;
    }

    @NotNull
    private String getName() {
        return name;
    }

    public Member getOwner() {
        return owner;
    }

    public long getOwnerIdLong() {
        return ownerId;
    }

    public boolean isMember(@NotNull User user) {
        return memberCache.get(user.getIdLong()) != null;
    }

    public Member getMember(@NotNull User user) {
        Checks.notNull(user, "User");
        return getMemberById(user.getIdLong());
    }

    @NotNull
    public MemberCacheView getMemberCache() {
        return memberCache;
    }

    @NotNull
    public SnowflakeCacheView<Role> getRoleCache() {
        return roleCache;
    }

    @NotNull
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return emoteCache;
    }

    @NotNull
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
    public Role getRoleById(@NotNull String id) {
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

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public void setMemberCount(int count) {
        this.memberCount = count;
    }

    public SortedSnowflakeCacheViewImpl<TextChannel> getTextChannelsView() {
        return textChannelCache;
    }

    public SortedSnowflakeCacheViewImpl<Role> getRolesView() {
        return roleCache;
    }

    public SnowflakeCacheViewImpl<Emote> getEmotesView() {
        return emoteCache;
    }

    public MemberCacheView getMembersView() {
        return memberCache;
    }

    public void acknowledgeMembers() {
        if (memberCache.size() == memberCount && !chunkingCallback.isDone()) {
            JDALogger.getLog(Guild.class).debug("Chunking completed for guild {}", this);
            chunkingCallback.complete(null);
        }
    }

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
