package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.handle.GuildSetupController;

import javax.annotation.Nonnull;


public class ReadyEvent extends Event {
    private final int availableGuilds;
    private final int unavailableGuilds;

    public ReadyEvent(@Nonnull JDA api, long responseNumber) {
        super(api, responseNumber);
        this.availableGuilds = (int) getJDA().getGuildCache().size();
        this.unavailableGuilds = ((JDAImpl) getJDA()).getGuildSetupController().getSetupNodes(GuildSetupController.Status.UNAVAILABLE).size();
    }


    public int getGuildAvailableCount() {
        return availableGuilds;
    }


    public int getGuildUnavailableCount() {
        return unavailableGuilds;
    }


    public int getGuildTotalCount() {
        return getGuildAvailableCount() + getGuildUnavailableCount();
    }
}
