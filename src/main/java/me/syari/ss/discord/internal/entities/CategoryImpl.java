

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import me.syari.ss.discord.api.requests.restaction.InviteAction;
import me.syari.ss.discord.api.requests.restaction.order.CategoryOrderAction;
import me.syari.ss.discord.internal.requests.CompletedRestAction;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryImpl extends AbstractChannelImpl<Category, CategoryImpl> implements Category
{
    public CategoryImpl(long id, GuildImpl guild)
    {
        super(id, guild);
    }

    @Override
    public CategoryImpl setPosition(int rawPosition)
    {
        getGuild().getCategoriesView().clearCachedLists();
        return super.setPosition(rawPosition);
    }

    @Override
    public Category getParent()
    {
        return null;
    }

    @Nonnull
    @Override
    public ChannelType getType()
    {
        return ChannelType.CATEGORY;
    }

    @Nonnull
    @Override
    public List<Member> getMembers()
    {
        return Collections.unmodifiableList(getChannels().stream()
                    .map(GuildChannel::getMembers)
                    .flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList()));
    }

    @Override
    public int getPosition()
    {
        //We call getCategories instead of directly accessing the GuildImpl.getCategories because
        // getCategories does the sorting logic.
        List<Category> channels = getGuild().getCategories();
        for (int i = 0; i < channels.size(); i++)
        {
            if (equals(channels.get(i)))
                return i;
        }
        throw new AssertionError("Somehow when determining position we never found the Category in the Guild's channels? wtf?");
    }

    @Nonnull
    @Override
    public ChannelAction<Category> createCopy(@Nonnull Guild guild)
    {
        Checks.notNull(guild, "Guild");
        ChannelAction<Category> action = guild.createCategory(name);
        if (guild.equals(getGuild()))
        {
            for (PermissionOverride o : overrides.valueCollection())
            {
                if (o.isMemberOverride())
                    action.addPermissionOverride(o.getMember(), o.getAllowedRaw(), o.getDeniedRaw());
                else
                    action.addPermissionOverride(o.getRole(), o.getAllowedRaw(), o.getDeniedRaw());
            }
        }
        return action;
    }

    @Nonnull
    @Override
    public InviteAction createInvite()
    {
        throw new UnsupportedOperationException("Cannot create invites for category!");
    }

    @Nonnull
    @Override
    public RestAction<List<Invite>> retrieveInvites()
    {
        return new CompletedRestAction<>(getJDA(), Collections.emptyList());
    }

    @Nonnull
    @Override
    public List<GuildChannel> getChannels()
    {
        List<GuildChannel> channels = new ArrayList<>();
        channels.addAll(getStoreChannels());
        channels.addAll(getTextChannels());
        channels.addAll(getVoiceChannels());
        Collections.sort(channels);
        return Collections.unmodifiableList(channels);
    }

    @Nonnull
    @Override
    public List<StoreChannel> getStoreChannels()
    {
        return Collections.unmodifiableList(getGuild().getStoreChannelCache().stream()
                    .filter(channel -> equals(channel.getParent()))
                    .sorted().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<TextChannel> getTextChannels()
    {
        return Collections.unmodifiableList(getGuild().getTextChannels().stream()
                    .filter(channel -> equals(channel.getParent()))
                    .sorted().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<VoiceChannel> getVoiceChannels()
    {
        return Collections.unmodifiableList(getGuild().getVoiceChannels().stream()
                    .filter(channel -> equals(channel.getParent()))
                    .sorted().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public ChannelAction<TextChannel> createTextChannel(@Nonnull String name)
    {
        ChannelAction<TextChannel> action = getGuild().createTextChannel(name).setParent(this);
        applyPermission(action);
        return action;
    }

    @Nonnull
    @Override
    public ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name)
    {
        ChannelAction<VoiceChannel> action = getGuild().createVoiceChannel(name).setParent(this);
        applyPermission(action);
        return action;
    }

    @Nonnull
    @Override
    public CategoryOrderAction modifyTextChannelPositions()
    {
        return getGuild().modifyTextChannelPositions(this);
    }

    @Nonnull
    @Override
    public CategoryOrderAction modifyVoiceChannelPositions()
    {
        return getGuild().modifyVoiceChannelPositions(this);
    }

    @Override
    public String toString()
    {
        return "GC:" + getName() + '(' + id + ')';
    }

    private void applyPermission(ChannelAction a)
    {
        overrides.forEachValue(override ->
        {
            if (override.isMemberOverride())
                a.addPermissionOverride(override.getMember(), override.getAllowedRaw(), override.getDeniedRaw());
            else
                a.addPermissionOverride(override.getRole(), override.getAllowedRaw(), override.getDeniedRaw());
            return true;
        });
    }
}
