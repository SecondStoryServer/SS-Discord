

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;

public class GuildMembersChunkHandler extends SocketHandler
{
    public GuildMembersChunkHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("guild_id");
        DataArray members = content.getArray("members");
        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        if (guild != null)
        {
            WebSocketClient.LOG.debug("Received member chunk for guild that is already in cache. GuildId: {} Count: {}", guildId, members.length());
            EntityBuilder builder = getJDA().getEntityBuilder();
            for (int i = 0; i < members.length(); i++)
            {
                DataObject object = members.getObject(i);
                builder.createMember(guild, object);
            }
            guild.acknowledgeMembers();
        }
        getJDA().getGuildSetupController().onMemberChunk(guildId, members);
        return null;
    }

}
