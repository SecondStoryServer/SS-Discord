

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class InviteImpl implements Invite
{
    private final JDAImpl api;
    private final Channel channel;
    private final String code;
    private final boolean expanded;
    private final Guild guild;
    private final Group group;
    private final User inviter;
    private final int maxAge;
    private final int maxUses;
    private final boolean temporary;
    private final OffsetDateTime timeCreated;
    private final int uses;
    private final Invite.InviteType type;

    public InviteImpl(final JDAImpl api, final String code, final boolean expanded, final User inviter,
            final int maxAge, final int maxUses, final boolean temporary, final OffsetDateTime timeCreated,
            final int uses, final Channel channel, final Guild guild, final Group group, final Invite.InviteType type)
    {
        this.api = api;
        this.code = code;
        this.expanded = expanded;
        this.inviter = inviter;
        this.maxAge = maxAge;
        this.maxUses = maxUses;
        this.temporary = temporary;
        this.timeCreated = timeCreated;
        this.uses = uses;
        this.channel = channel;
        this.guild = guild;
        this.group = group;
        this.type = type;
    }

    public static RestAction<Invite> resolve(final JDA api, final String code, final boolean withCounts)
    {
        Checks.notNull(code, "code");
        Checks.notNull(api, "api");

        Route.CompiledRoute route = Route.Invites.GET_INVITE.compile(code);
        
        if (withCounts)
            route = route.withQueryParams("with_counts", "true");

        JDAImpl jda = (JDAImpl) api;
        return new RestActionImpl<>(api, route, (response, request) ->
                jda.getEntityBuilder().createInvite(response.getObject()));
    }

    @Nonnull
    @Override
    public String getCode()
    {
        return this.code;
    }

    @Nonnull
    @Override
    @Deprecated
    @DeprecatedSince("4.BETA.0")
    @ReplaceWith("getTimeCreated()")
    public OffsetDateTime getCreationTime()
    {
        return getTimeCreated();
    }

    @Nonnull
    @Override
    public JDAImpl getJDA()
    {
        return this.api;
    }

    @Nonnull
    @Override
    public OffsetDateTime getTimeCreated()
    {
        if (!this.expanded)
            throw new IllegalStateException("Only valid for expanded invites");
        return this.timeCreated;
    }

    @Override
    public int hashCode()
    {
        return code.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof InviteImpl))
            return false;
        InviteImpl impl = (InviteImpl) obj;
        return impl.code.equals(this.code);
    }

    @Override
    public String toString()
    {
        return "Invite(" + this.code + ")";
    }

    public static class ChannelImpl implements Channel
    {
        private final long id;
        private final String name;
        private final ChannelType type;

        public ChannelImpl(final long id, final String name, final ChannelType type)
        {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        @Override
        public long getIdLong()
        {
            return id;
        }

        @Nonnull
        @Override
        public ChannelType getType()
        {
            return this.type;
        }

    }

    public static class GuildImpl implements Guild
    {
        private final String iconId, name, splashId;
        private final int presenceCount, memberCount;
        private final long id;
        private final me.syari.ss.discord.api.entities.Guild.VerificationLevel verificationLevel;
        private final Set<String> features;

        public GuildImpl(final long id, final String iconId, final String name, final String splashId,
                         final me.syari.ss.discord.api.entities.Guild.VerificationLevel verificationLevel, final int presenceCount, final int memberCount, final Set<String> features)
        {
            this.id = id;
            this.iconId = iconId;
            this.name = name;
            this.splashId = splashId;
            this.verificationLevel = verificationLevel;
            this.presenceCount = presenceCount;
            this.memberCount = memberCount;
            this.features = features;
        }

        @Override
        public long getIdLong()
        {
            return id;
        }

    }

    public static class GroupImpl implements Group
    {

        private final String iconId, name;
        private final long id;
        private final List<String> users;

        public GroupImpl(final String iconId, final String name, final long id, final List<String> users)
        {
            this.iconId = iconId;
            this.name = name;
            this.id = id;
            this.users = users;
        }

        @Override
        public long getIdLong()
        {
            return id;
        }

    }
}
