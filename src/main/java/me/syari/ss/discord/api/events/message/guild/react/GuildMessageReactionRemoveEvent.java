package me.syari.ss.discord.api.events.message.guild.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildMessageReactionRemoveEvent extends GenericGuildMessageReactionEvent {
    public GuildMessageReactionRemoveEvent(@Nonnull JDA api, long responseNumber, @Nullable Member member, @Nonnull MessageReaction reaction, long userId) {
        super(api, responseNumber, member, reaction, userId);
    }
}
