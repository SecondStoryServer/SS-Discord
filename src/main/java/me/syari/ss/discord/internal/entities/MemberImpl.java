

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.PermissionUtil;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemberImpl implements Member
{
    private static final ZoneOffset OFFSET = ZoneOffset.of("+00:00");
    private final SnowflakeReference<Guild> guild;
    private final User user;
    private final JDAImpl api;
    private final Set<Role> roles = ConcurrentHashMap.newKeySet();
    private final GuildVoiceState voiceState;
    private final Map<ClientType, OnlineStatus> clientStatus;

    private String nickname;
    private long joinDate, boostDate;
    private List<Activity> activities = null;
    private OnlineStatus onlineStatus = OnlineStatus.OFFLINE;

    public MemberImpl(GuildImpl guild, User user)
    {
        this.api = (JDAImpl) user.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
        this.user = user;
        boolean cacheState = api.isCacheFlagSet(CacheFlag.VOICE_STATE) || user.equals(api.getSelfUser());
        boolean cacheOnline = api.isCacheFlagSet(CacheFlag.CLIENT_STATUS);
        this.voiceState = cacheState ? new GuildVoiceStateImpl(this) : null;
        this.clientStatus = cacheOnline ? Collections.synchronizedMap(new EnumMap<>(ClientType.class)) : null;
    }

    @Nonnull
    @Override
    public User getUser()
    {
        return user;
    }

    @Nonnull
    @Override
    public GuildImpl getGuild()
    {
        return (GuildImpl) guild.resolve();
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return api;
    }

    @Nonnull
    @Override
    public OffsetDateTime getTimeJoined()
    {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(joinDate), OFFSET);
    }

    @Nullable
    @Override
    public OffsetDateTime getTimeBoosted()
    {
        return boostDate != 0 ? OffsetDateTime.ofInstant(Instant.ofEpochMilli(boostDate), OFFSET) : null;
    }

    @Override
    public GuildVoiceState getVoiceState()
    {
        return voiceState;
    }

    @Nonnull
    @Override
    public List<Activity> getActivities()
    {
        return activities == null || activities.isEmpty() ? Collections.emptyList() : activities;
    }

    @Nonnull
    @Override
    public OnlineStatus getOnlineStatus()
    {
        return onlineStatus;
    }

    @Override
    public String getNickname()
    {
        return nickname;
    }

    @Nonnull
    @Override
    public String getEffectiveName()
    {
        return nickname != null ? nickname : getUser().getName();
    }

    @Nonnull
    @Override
    public List<Role> getRoles()
    {
        List<Role> roleList = new ArrayList<>(roles);
        roleList.sort(Comparator.reverseOrder());

        return Collections.unmodifiableList(roleList);
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissions()
    {
        return Permission.getPermissions(PermissionUtil.getEffectivePermission(this));
    }

    @Override
    public boolean hasPermission(@Nonnull Permission... permissions)
    {
        return PermissionUtil.checkPermission(this, permissions);
    }

    @Override
    public boolean hasPermission(@Nonnull Collection<Permission> permissions)
    {
        Checks.notNull(permissions, "Permission Collection");

        return hasPermission(permissions.toArray(Permission.EMPTY_PERMISSIONS));
    }

    @Override
    public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions)
    {
        return PermissionUtil.checkPermission(channel, this, permissions);
    }

    @Override
    public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions)
    {
        Checks.notNull(permissions, "Permission Collection");

        return hasPermission(channel, permissions.toArray(Permission.EMPTY_PERMISSIONS));
    }

    @Override
    public boolean canInteract(@Nonnull Member member)
    {
        return PermissionUtil.canInteract(this, member);
    }

    @Override
    public boolean canInteract(@Nonnull Role role)
    {
        return PermissionUtil.canInteract(this, role);
    }

    @Override
    public boolean isOwner()
    {
        return this.user.getIdLong() == getGuild().getOwnerIdLong();
    }

    @Override
    public boolean isFake()
    {
        return getGuild().getMemberById(getIdLong()) == null;
    }

    @Override
    public long getIdLong()
    {
        return user.getIdLong();
    }

    public MemberImpl setNickname(String nickname)
    {
        this.nickname = nickname;
        return this;
    }

    public MemberImpl setJoinDate(long joinDate)
    {
        this.joinDate = joinDate;
        return this;
    }

    public MemberImpl setBoostDate(long boostDate)
    {
        this.boostDate = boostDate;
        return this;
    }

    public MemberImpl setActivities(List<Activity> activities)
    {
        this.activities = Collections.unmodifiableList(activities);
        return this;
    }

    public MemberImpl setOnlineStatus(ClientType type, OnlineStatus status)
    {
        if (this.clientStatus == null || type == ClientType.UNKNOWN || type == null)
            return this;
        if (status == null || status == OnlineStatus.UNKNOWN || status == OnlineStatus.OFFLINE)
            this.clientStatus.remove(type);
        else
            this.clientStatus.put(type, status);
        return this;
    }

    public MemberImpl setOnlineStatus(OnlineStatus onlineStatus)
    {
        this.onlineStatus = onlineStatus;
        return this;
    }

    public Set<Role> getRoleSet()
    {
        return roles;
    }

    public long getBoostDateRaw()
    {
        return boostDate;
    }

    public boolean isIncomplete()
    {
        // the joined_at is only present on complete members, this implies the member is completely loaded
        return !isOwner() && Objects.equals(getGuild().getTimeCreated(), getTimeJoined());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof MemberImpl))
            return false;

        MemberImpl oMember = (MemberImpl) o;
        return oMember.user.getIdLong() == user.getIdLong()
            && oMember.guild.getIdLong() == guild.getIdLong();
    }

    @Override
    public int hashCode()
    {
        return (guild.getIdLong() + user.getId()).hashCode();
    }

    @Override
    public String toString()
    {
        return "MB:" + getEffectiveName() + '(' + getUser().toString() + " / " + getGuild().toString() +')';
    }

    @Nonnull
    @Override
    public String getAsMention()
    {
        return (nickname == null ? "<@" : "<@!") + user.getId() + '>';
    }

}
