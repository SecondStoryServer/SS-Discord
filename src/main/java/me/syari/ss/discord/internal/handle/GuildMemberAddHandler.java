
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.guild.member.GuildMemberJoinEvent;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;

public class GuildMemberAddHandler extends SocketHandler
{

    public GuildMemberAddHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long id = content.getLong("guild_id");
        boolean setup = getJDA().getGuildSetupController().onAddMember(id, content);
        if (setup)
            return null;

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(id);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, id, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Caching member for guild that is not yet cached. Guild ID: {} JSON: {}", id, content);
            return null;
        }

        long userId = content.getObject("user").getUnsignedLong("id");
        if (guild.getMemberById(userId) != null)
        {
            WebSocketClient.LOG.debug("Ignoring duplicate GUILD_MEMBER_ADD for user with id {} in guild {}", userId, id);
            return null;
        }

        // Update memberCount
        guild.onMemberAdd();
        Member member = getJDA().getEntityBuilder().createMember(guild, content);
        getJDA().handleEvent(
            new GuildMemberJoinEvent(
                getJDA(), responseNumber,
                member));
        return null;
    }
}
