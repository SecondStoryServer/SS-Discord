package me.syari.ss.discord.internal.managers;

import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.managers.Presence;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;

import javax.annotation.Nonnull;


public class PresenceImpl implements Presence {
    private final JDAImpl api;
    private boolean idle = false;
    private Activity activity = null;
    private OnlineStatus status = OnlineStatus.ONLINE;


    public PresenceImpl(JDAImpl jda) {
        this.api = jda;
    }


    /* -- Public Getters -- */

    @Nonnull
    @Override
    public OnlineStatus getStatus() {
        return status;
    }

    /* -- Impl Setters -- */

    public void setCacheStatus(OnlineStatus status) {
        if (status == null)
            throw new NullPointerException("Null OnlineStatus is not allowed.");
        if (status == OnlineStatus.OFFLINE)
            status = OnlineStatus.INVISIBLE;
        this.status = status;
    }

    public PresenceImpl setCacheActivity(Activity game) {
        this.activity = game;
        return this;
    }

    public PresenceImpl setCacheIdle(boolean idle) {
        this.idle = idle;
        return this;
    }


    /* -- Internal Methods -- */

    public DataObject getFullPresence() {
        DataObject activity = getGameJson(this.activity);
        return DataObject.empty()
                .put("afk", idle)
                .put("since", System.currentTimeMillis())
                .put("game", activity)
                .put("status", getStatus().getKey());
    }

    private DataObject getGameJson(Activity activity) {
        if (activity == null) {
            return null;
        } else {
            activity.getType();
        }
        DataObject gameObj = DataObject.empty();
        gameObj.put("name", activity.getName());
        gameObj.put("type", activity.getType().getKey());
        if (activity.getUrl() != null)
            gameObj.put("url", activity.getUrl());

        return gameObj;
    }
}
