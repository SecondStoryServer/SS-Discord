package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryImpl extends AbstractChannelImpl<Category, CategoryImpl> implements Category {
    public CategoryImpl(long id, GuildImpl guild) {
        super(id, guild);
    }

    @Override
    public CategoryImpl setPosition(int rawPosition) {
        getGuild().getCategoriesView().clearCachedLists();
        return super.setPosition(rawPosition);
    }

    @Override
    public Category getParent() {
        return null;
    }

    @Nonnull
    @Override
    public ChannelType getType() {
        return ChannelType.CATEGORY;
    }

    @Nonnull
    @Override
    public List<Member> getMembers() {
        return Collections.unmodifiableList(getChannels().stream()
                .map(GuildChannel::getMembers)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<GuildChannel> getChannels() {
        List<GuildChannel> channels = new ArrayList<>();
        channels.addAll(getStoreChannels());
        channels.addAll(getTextChannels());
        channels.addAll(getVoiceChannels());
        Collections.sort(channels);
        return Collections.unmodifiableList(channels);
    }

    @Nonnull
    @Override
    public List<StoreChannel> getStoreChannels() {
        return Collections.unmodifiableList(getGuild().getStoreChannelCache().stream()
                .filter(channel -> equals(channel.getParent()))
                .sorted().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<TextChannel> getTextChannels() {
        return Collections.unmodifiableList(getGuild().getTextChannels().stream()
                .filter(channel -> equals(channel.getParent()))
                .sorted().collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<VoiceChannel> getVoiceChannels() {
        return Collections.unmodifiableList(getGuild().getVoiceChannels().stream()
                .filter(channel -> equals(channel.getParent()))
                .sorted().collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "GC:" + getName() + '(' + id + ')';
    }

}
