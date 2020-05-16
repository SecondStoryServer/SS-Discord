package me.syari.ss.discord.api.events.message.guild.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.message.guild.GenericGuildMessageEvent;

import javax.annotation.Nonnull;


public class GuildMessageReactionRemoveAllEvent extends GenericGuildMessageEvent {
    public GuildMessageReactionRemoveAllEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel) {
        super(api, responseNumber, messageId, channel);
    }
}
