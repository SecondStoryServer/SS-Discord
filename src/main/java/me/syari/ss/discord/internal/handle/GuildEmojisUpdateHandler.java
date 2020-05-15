

package me.syari.ss.discord.internal.handle;

import gnu.trove.map.TLongObjectMap;
import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.events.emote.EmoteAddedEvent;
import me.syari.ss.discord.api.events.emote.EmoteRemovedEvent;
import me.syari.ss.discord.api.events.emote.update.EmoteUpdateNameEvent;
import me.syari.ss.discord.api.events.emote.update.EmoteUpdateRolesEvent;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EmoteImpl;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GuildEmojisUpdateHandler extends SocketHandler
{
    public GuildEmojisUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        if (!getJDA().isCacheFlagSet(CacheFlag.EMOTE))
            return null;
        final long guildId = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, responseNumber, allContent, this::handle);
            return null;
        }

        DataArray array = content.getArray("emojis");
        List<Emote> oldEmotes, newEmotes;
        SnowflakeCacheViewImpl<Emote> emoteView = guild.getEmotesView();
        try (UnlockHook hook = emoteView.writeLock())
        {
            TLongObjectMap<Emote> emoteMap = emoteView.getMap();
            oldEmotes = new ArrayList<>(emoteMap.valueCollection()); //snapshot of emote cache
            newEmotes = new ArrayList<>();
            for (int i = 0; i < array.length(); i++)
            {
                DataObject current = array.getObject(i);
                final long emoteId = current.getLong("id");
                EmoteImpl emote = (EmoteImpl) emoteMap.get(emoteId);
                EmoteImpl oldEmote = null;

                if (emote == null)
                {
                    emote = new EmoteImpl(emoteId, guild);
                    newEmotes.add(emote);
                }
                else
                {
                    // emote is in our cache which is why we don't want to remove it in cleanup later
                    oldEmotes.remove(emote);
                    oldEmote = emote.clone();
                }

                emote.setName(current.getString("name"))
                     .setAnimated(current.getBoolean("animated"))
                     .setManaged(current.getBoolean("managed"));
                //update roles
                DataArray roles = current.getArray("roles");
                Set<Role> newRoles = emote.getRoleSet();
                Set<Role> oldRoles = new HashSet<>(newRoles); //snapshot of cached roles
                for (int j = 0; j < roles.length(); j++)
                {
                    Role role = guild.getRoleById(roles.getString(j));
                    if (role != null)
                    {
                        newRoles.add(role);
                        oldRoles.remove(role);
                    }
                }

                //cleanup old cached roles that were not found in the JSONArray
                for (Role r : oldRoles)
                {
                    // newRoles directly writes to the set contained in the emote
                    newRoles.remove(r);
                }

                // finally, update the emote
                emoteMap.put(emote.getIdLong(), emote);
                // check for updated fields and fire events
                handleReplace(oldEmote, emote);
            }
            for (Emote e : oldEmotes)
                emoteMap.remove(e.getIdLong());
        }
        //cleanup old emotes that don't exist anymore
        for (Emote e : oldEmotes)
        {
            getJDA().handleEvent(
                new EmoteRemovedEvent(
                    getJDA(), responseNumber,
                    e));
        }

        for (Emote e : newEmotes)
        {
            getJDA().handleEvent(
                new EmoteAddedEvent(
                    getJDA(), responseNumber,
                    e));
        }

        return null;
    }

    private void handleReplace(Emote oldEmote, Emote newEmote)
    {
        if (oldEmote == null || newEmote == null) return;

        if (!Objects.equals(oldEmote.getName(), newEmote.getName()))
        {
            getJDA().handleEvent(
                new EmoteUpdateNameEvent(
                    getJDA(), responseNumber,
                    newEmote, oldEmote.getName()));
        }

        if (!CollectionUtils.isEqualCollection(oldEmote.getRoles(), newEmote.getRoles()))
        {
            getJDA().handleEvent(
                new EmoteUpdateRolesEvent(
                    getJDA(), responseNumber,
                    newEmote, oldEmote.getRoles()));
        }

    }

}
