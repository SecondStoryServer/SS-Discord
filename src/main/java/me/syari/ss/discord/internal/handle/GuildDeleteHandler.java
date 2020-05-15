

package me.syari.ss.discord.internal.handle;

import gnu.trove.set.TLongSet;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.events.guild.GuildLeaveEvent;
import me.syari.ss.discord.api.events.guild.GuildUnavailableEvent;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.entities.PrivateChannelImpl;
import me.syari.ss.discord.internal.entities.UserImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.AbstractCacheView;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;

public class GuildDeleteHandler extends SocketHandler
{
    public GuildDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long id = content.getLong("id");
        GuildSetupController setupController = getJDA().getGuildSetupController();
        boolean wasInit = setupController.onDelete(id, content);
        if (wasInit || setupController.isUnavailable(id))
            return null;

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(id);
        boolean unavailable = content.getBoolean("unavailable");
        if (guild == null)
        {
            //getJDA().getEventCache().cache(EventCache.Type.GUILD, id, () -> handle(responseNumber, allContent));
            WebSocketClient.LOG.debug("Received GUILD_DELETE for a Guild that is not currently cached. ID: {} unavailable: {}", id, unavailable);
            return null;
        }

        //If the event is attempting to mark the guild as unavailable, but it is already unavailable,
        // ignore the event
        if (setupController.isUnavailable(id) && unavailable)
            return null;

        //Remove everything from global cache
        // this prevents some race-conditions for getting audio managers from guilds
        SnowflakeCacheViewImpl<Guild> guildView = getJDA().getGuildsView();
        SnowflakeCacheViewImpl<StoreChannel> storeView = getJDA().getStoreChannelsView();
        SnowflakeCacheViewImpl<TextChannel> textView = getJDA().getTextChannelsView();
        SnowflakeCacheViewImpl<VoiceChannel> voiceView = getJDA().getVoiceChannelsView();
        SnowflakeCacheViewImpl<Category> categoryView = getJDA().getCategoriesView();
        guildView.remove(id);
        try (UnlockHook hook = storeView.writeLock())
        {
            guild.getStoreChannelCache()
                 .forEachUnordered(chan -> storeView.getMap().remove(chan.getIdLong()));
        }
        try (UnlockHook hook = textView.writeLock())
        {
            guild.getTextChannelCache()
                 .forEachUnordered(chan -> textView.getMap().remove(chan.getIdLong()));
        }
        try (UnlockHook hook = voiceView.writeLock())
        {
            guild.getVoiceChannelCache()
                 .forEachUnordered(chan -> voiceView.getMap().remove(chan.getIdLong()));
        }
        try (UnlockHook hook = categoryView.writeLock())
        {
            guild.getCategoryCache()
                 .forEachUnordered(chan -> categoryView.getMap().remove(chan.getIdLong()));
        }

        //cleaning up all users that we do not share a guild with anymore
        // Anything left in memberIds will be removed from the main userMap
        //Use a new HashSet so that we don't actually modify the Member map so it doesn't affect Guild#getMembers for the leave event.
        TLongSet memberIds = guild.getMembersView().keySet(); // copies keys
        getJDA().getGuildCache().stream()
                .map(GuildImpl.class::cast)
                .forEach(g -> memberIds.removeAll(g.getMembersView().keySet()));
        // Remember, everything left in memberIds is removed from the userMap
        SnowflakeCacheViewImpl<User> userView = getJDA().getUsersView();
        try (UnlockHook hook = userView.writeLock())
        {
            long selfId = getJDA().getSelfUser().getIdLong();
            memberIds.forEach(memberId -> {
                if (memberId == selfId)
                    return true; // don't remove selfUser from cache
                UserImpl user = (UserImpl) userView.getMap().remove(memberId);
                if (user.hasPrivateChannel())
                {
                    PrivateChannelImpl priv = (PrivateChannelImpl) user.getPrivateChannel();
                    user.setFake(true);
                    priv.setFake(true);
                    getJDA().getFakeUserMap().put(user.getIdLong(), user);
                    getJDA().getFakePrivateChannelMap().put(priv.getIdLong(), priv);
                }
                getJDA().getEventCache().clear(EventCache.Type.USER, memberId);
                return true;
            });
        }

        if (unavailable)
        {
            setupController.onUnavailable(id);
            guild.setAvailable(false);
            getJDA().handleEvent(
                new GuildUnavailableEvent(
                    getJDA(), responseNumber,
                    guild));
        }
        else
        {
            getJDA().handleEvent(
                new GuildLeaveEvent(
                    getJDA(), responseNumber,
                    guild));
        }
        getJDA().getEventCache().clear(EventCache.Type.GUILD, id);
        return null;
    }
}
