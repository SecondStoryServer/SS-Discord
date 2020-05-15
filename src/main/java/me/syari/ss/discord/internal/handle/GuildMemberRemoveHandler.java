
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.events.guild.member.GuildMemberLeaveEvent;
import me.syari.ss.discord.api.events.guild.voice.GuildVoiceLeaveEvent;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.*;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;

public class GuildMemberRemoveHandler extends SocketHandler
{

    public GuildMemberRemoveHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long id = content.getLong("guild_id");
        boolean setup = getJDA().getGuildSetupController().onRemoveMember(id, content);
        if (setup)
            return null;

        GuildImpl guild = (GuildImpl) getJDA().getGuildsView().get(id);
        if (guild == null)
        {
            //We probably just left the guild and this event is trying to remove us from the guild, therefore ignore
            return null;
        }

        final long userId = content.getObject("user").getUnsignedLong("id");
        if (userId == getJDA().getSelfUser().getIdLong())
        {
            //We probably just left the guild and this event is trying to remove us from the guild, therefore ignore
            return null;
        }
        MemberImpl member = (MemberImpl) guild.getMembersView().remove(userId);

        if (member == null)
        {
            WebSocketClient.LOG.debug("Received GUILD_MEMBER_REMOVE for a Member that does not exist in the specified Guild. UserId: {} GuildId: {}", userId, id);
            return null;
        }

        // Update the memberCount
        guild.onMemberRemove();

        GuildVoiceStateImpl voiceState = (GuildVoiceStateImpl) member.getVoiceState();
        if (voiceState != null && voiceState.inVoiceChannel())//If this user was in a VoiceChannel, fire VoiceLeaveEvent.
        {
            VoiceChannel channel = voiceState.getChannel();
            voiceState.setConnectedChannel(null);
            ((VoiceChannelImpl) channel).getConnectedMembersMap().remove(member.getUser().getIdLong());
            getJDA().handleEvent(
                    new GuildVoiceLeaveEvent(
                            getJDA(), responseNumber,
                            member, channel));
        }

        //The user is not in a different guild that we share
        SnowflakeCacheViewImpl<User> userView = getJDA().getUsersView();
        try (UnlockHook hook = userView.writeLock())
        {
            if (userId != getJDA().getSelfUser().getIdLong() // don't remove selfUser from cache
                    && getJDA().getGuildsView().stream()
                               .map(GuildImpl.class::cast)
                               .noneMatch(g -> g.getMembersView().get(userId) != null))
            {
                UserImpl user = (UserImpl) userView.getMap().remove(userId);
                if (user.hasPrivateChannel())
                {
                    PrivateChannelImpl priv = (PrivateChannelImpl) user.getPrivateChannel();
                    user.setFake(true);
                    priv.setFake(true);
                    getJDA().getFakeUserMap().put(user.getIdLong(), user);
                    getJDA().getFakePrivateChannelMap().put(priv.getIdLong(), priv);
                }
                getJDA().getEventCache().clear(EventCache.Type.USER, userId);
            }
        }
        getJDA().handleEvent(
            new GuildMemberLeaveEvent(
                getJDA(), responseNumber,
                member));
        return null;
    }
}
