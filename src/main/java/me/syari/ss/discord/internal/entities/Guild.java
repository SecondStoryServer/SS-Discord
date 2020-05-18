package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.cache.MemberCacheView;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class Guild implements ISnowflake {
    private final long id;
    private final JDA api;

    private final SnowflakeCacheView<TextChannel> textChannelCache = new SnowflakeCacheView<>(TextChannel.class);
    private final SnowflakeCacheView<Role> roleCache = new SnowflakeCacheView<>(Role.class);
    private final SnowflakeCacheView<Emote> emoteCache = new SnowflakeCacheView<>(Emote.class);
    private final MemberCacheView memberCache = new MemberCacheView();

    private final CompletableFuture<Void> chunkingCallback = new CompletableFuture<>();

    private Member owner;
    private String name;
    private long ownerId;
    private int memberCount;

    public Guild(JDA api, long id) {
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
        return getMemberById(user.getIdLong());
    }

    @NotNull
    public MemberCacheView getMemberCache() {
        return memberCache;
    }

    @NotNull
    public ISnowflakeCacheView<Role> getRoleCache() {
        return roleCache;
    }

    @NotNull
    public ISnowflakeCacheView<Emote> getEmoteCache() {
        return emoteCache;
    }

    @NotNull
    public JDA getJDA() {
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

    public SnowflakeCacheView<TextChannel> getTextChannelsView() {
        return textChannelCache;
    }

    public SnowflakeCacheView<Role> getRolesView() {
        return roleCache;
    }

    public SnowflakeCacheView<Emote> getEmotesView() {
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
