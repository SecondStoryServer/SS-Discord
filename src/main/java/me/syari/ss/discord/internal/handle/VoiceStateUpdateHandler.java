

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.guild.voice.*;
import me.syari.ss.discord.api.hooks.VoiceDispatchInterceptor;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.*;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.MemberCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;

import java.util.Objects;
import java.util.Optional;

public class VoiceStateUpdateHandler extends SocketHandler
{
    public VoiceStateUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final Long guildId = content.isNull("guild_id") ? null : content.getLong("guild_id");
        if (guildId == null)
            return null; //unhandled for calls
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;
        handleGuildVoiceState(content);
        return null;
    }

    private void handleGuildVoiceState(DataObject content)
    {
        final long userId = content.getLong("user_id");
        final long guildId = content.getLong("guild_id");
        final Long channelId = !content.isNull("channel_id") ? content.getLong("channel_id") : null;
        final String sessionId = !content.isNull("session_id") ? content.getString("session_id") : null;
        boolean selfMuted = content.getBoolean("self_mute");
        boolean selfDeafened = content.getBoolean("self_deaf");
        boolean guildMuted = content.getBoolean("mute");
        boolean guildDeafened = content.getBoolean("deaf");
        boolean suppressed = content.getBoolean("suppress");

        Guild guild = getJDA().getGuildById(guildId);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Received a VOICE_STATE_UPDATE for a Guild that has yet to be cached. JSON: {}", content);
            return;
        }

        VoiceChannelImpl channel = channelId != null ? (VoiceChannelImpl) guild.getVoiceChannelById(channelId) : null;
        if (channel == null && channelId != null)
        {
            getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Received VOICE_STATE_UPDATE for a VoiceChannel that has yet to be cached. JSON: {}", content);
            return;
        }

        MemberImpl member = getLazyMember(content, userId, (GuildImpl) guild, channelId != null);
        if (member == null) return;

        GuildVoiceStateImpl vState = (GuildVoiceStateImpl) member.getVoiceState();
        if (vState == null)
            return;
        vState.setSessionId(sessionId); //Cant really see a reason for an event for this
        VoiceDispatchInterceptor voiceInterceptor = getJDA().getVoiceInterceptor();
        boolean isSelf = guild.getSelfMember().equals(member);

        boolean wasMute = vState.isMuted();
        boolean wasDeaf = vState.isDeafened();

        if (selfMuted != vState.isSelfMuted())
        {
            vState.setSelfMuted(selfMuted);
            getJDA().handleEvent(new GuildVoiceSelfMuteEvent(getJDA(), responseNumber, member));
        }
        if (selfDeafened != vState.isSelfDeafened())
        {
            vState.setSelfDeafened(selfDeafened);
            getJDA().handleEvent(new GuildVoiceSelfDeafenEvent(getJDA(), responseNumber, member));
        }
        if (guildMuted != vState.isGuildMuted())
        {
            vState.setGuildMuted(guildMuted);
            getJDA().handleEvent(new GuildVoiceGuildMuteEvent(getJDA(), responseNumber, member));
        }
        if (guildDeafened != vState.isGuildDeafened())
        {
            vState.setGuildDeafened(guildDeafened);
            getJDA().handleEvent(new GuildVoiceGuildDeafenEvent(getJDA(), responseNumber, member));
        }
        if (suppressed != vState.isSuppressed())
        {
            vState.setSuppressed(suppressed);
            getJDA().handleEvent(new GuildVoiceSuppressEvent(getJDA(), responseNumber, member));
        }
        if (wasMute != vState.isMuted())
            getJDA().handleEvent(new GuildVoiceMuteEvent(getJDA(), responseNumber, member));
        if (wasDeaf != vState.isDeafened())
            getJDA().handleEvent(new GuildVoiceDeafenEvent(getJDA(), responseNumber, member));
            
        if (!Objects.equals(channel, vState.getChannel()))
        {
            VoiceChannelImpl oldChannel = (VoiceChannelImpl) vState.getChannel();
            vState.setConnectedChannel(channel);

            if (oldChannel == null)
            {
                channel.getConnectedMembersMap().put(userId, member);
                getJDA().handleEvent(
                        new GuildVoiceJoinEvent(
                                getJDA(), responseNumber,
                                member));
            }
            else if (channel == null)
            {
                oldChannel.getConnectedMembersMap().remove(userId);
                getJDA().handleEvent(
                        new GuildVoiceLeaveEvent(
                                getJDA(), responseNumber,
                                member, oldChannel));
            }
            else
            {
                channel.getConnectedMembersMap().put(userId, member);
                oldChannel.getConnectedMembersMap().remove(userId);
                getJDA().handleEvent(
                        new GuildVoiceMoveEvent(
                                getJDA(), responseNumber,
                                member, oldChannel));
            }
        }
    }

    private MemberImpl getLazyMember(DataObject content, long userId, GuildImpl guild, boolean connected)
    {
        // Check for existing member
        Optional<DataObject> memberJson = content.optObject("member");
        MemberImpl member = (MemberImpl) guild.getMemberById(userId);
        if (!memberJson.isPresent() || userId == getJDA().getSelfUser().getIdLong())
            return member;

        // Handle cache changes
        boolean subscriptions = getJDA().isGuildSubscriptions();
        if (member == null)
        {
            if (connected && (subscriptions || getJDA().isCacheFlagSet(CacheFlag.VOICE_STATE)))
            {
                // the member just connected to a voice channel, otherwise we would know about it already!
                member = loadMember(userId, guild, memberJson.get(), "Initializing");
            }
        }
        else
        {
            if (subscriptions && member.isIncomplete())
            {
                // the member can be updated with new information that was missing before
                member = loadMember(userId, guild, memberJson.get(), "Updating");
            }
            else if (!subscriptions && !connected)
            {
                EntityBuilder.LOG.debug("Unloading member who just left a voice channel {}", memberJson);
                // the member just disconnected from the voice channel - remove it from cache
                unloadMember(userId, member);
                return null;
            }
        }
        return member;
    }

    @SuppressWarnings("ConstantConditions")
    private void unloadMember(long userId, MemberImpl member)
    {
        MemberCacheViewImpl membersView = member.getGuild().getMembersView();
        VoiceChannelImpl channelLeft = (VoiceChannelImpl) member.getVoiceState().getChannel();
        ((GuildVoiceStateImpl) member.getVoiceState()).setConnectedChannel(null);
        if (channelLeft != null)
            channelLeft.getConnectedMembersMap().remove(userId);
        getJDA().handleEvent(
            new GuildVoiceLeaveEvent(
                getJDA(), responseNumber,
                member, channelLeft));
        membersView.remove(userId);
        User user = member.getUser();
        boolean dropUser = getJDA().getGuildsView().applyStream(stream -> stream.noneMatch(it -> it.isMember(user)));
        if (dropUser)
            getJDA().getUsersView().remove(userId);
    }

    private MemberImpl loadMember(long userId, GuildImpl guild, DataObject memberJson, String comment)
    {
        EntityBuilder entityBuilder = getJDA().getEntityBuilder();
        MemberCacheViewImpl membersView = guild.getMembersView();
        SnowflakeCacheViewImpl<User> usersView = getJDA().getUsersView();
        MemberImpl member;
        EntityBuilder.LOG.debug("{} member from VOICE_STATE_UPDATE {}", comment, memberJson);
        member = entityBuilder.createMember(guild, memberJson);
        try (UnlockHook h1 = membersView.writeLock();
             UnlockHook h2 = usersView.writeLock())
        {
            membersView.getMap().put(userId, member);
            usersView.getMap().put(userId, member.getUser());
        }
        return member;
    }
}
