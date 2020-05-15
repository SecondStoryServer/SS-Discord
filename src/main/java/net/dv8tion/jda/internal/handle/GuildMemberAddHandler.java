
package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

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
