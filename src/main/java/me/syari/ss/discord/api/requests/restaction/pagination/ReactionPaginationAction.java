

package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public interface ReactionPaginationAction extends PaginationAction<User, ReactionPaginationAction>
{

    @Nonnull
    MessageReaction getReaction();
}
