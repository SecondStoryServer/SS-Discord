package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.RichPresence;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RichPresenceImpl extends ActivityImpl implements RichPresence {
    protected final long applicationId;

    protected final Party party;
    protected final String details;
    protected final String state;
    protected final Image largeImage;
    protected final Image smallImage;
    protected final String sessionId;
    protected final String syncId;
    protected final int flags;

    protected RichPresenceImpl(
            ActivityType type, String name, String url, long applicationId,
            Emoji emoji, Party party, String details, String state, Timestamps timestamps, String syncId, String sessionId,
            int flags, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText) {
        super(name, url, type, timestamps);
        this.applicationId = applicationId;
        this.party = party;
        this.details = details;
        this.state = state;
        this.sessionId = sessionId;
        this.syncId = syncId;
        this.flags = flags;
        this.largeImage = largeImageKey != null ? new Image(applicationId, largeImageKey, largeImageText) : null;
        this.smallImage = smallImageKey != null ? new Image(applicationId, smallImageKey, smallImageText) : null;
    }

    @Nonnull
    @Override
    public String getApplicationId() {
        return Long.toUnsignedString(applicationId);
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return String.format("RichPresence(%s / %s)", name, getApplicationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId, state, details, party, sessionId, syncId, flags, timestamps, largeImage, smallImage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RichPresenceImpl))
            return false;
        RichPresenceImpl p = (RichPresenceImpl) o;
        return applicationId == p.applicationId
                && Objects.equals(name, p.name)
                && Objects.equals(url, p.url)
                && Objects.equals(type, p.type)
                && Objects.equals(state, p.state)
                && Objects.equals(details, p.details)
                && Objects.equals(party, p.party)
                && Objects.equals(sessionId, p.sessionId)
                && Objects.equals(syncId, p.syncId)
                && Objects.equals(flags, p.flags)
                && Objects.equals(timestamps, p.timestamps)
                && Objects.equals(largeImage, p.largeImage)
                && Objects.equals(smallImage, p.smallImage);
    }
}
