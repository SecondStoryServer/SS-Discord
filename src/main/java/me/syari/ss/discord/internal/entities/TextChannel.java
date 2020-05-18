package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.MessageAction;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;
import org.jetbrains.annotations.NotNull;

public class TextChannel implements ISnowflake, Comparable<TextChannel> {
    protected final long id;
    protected final SnowflakeReference<Guild> guild;
    protected final JDAImpl api;
    protected String name;

    public TextChannel(long id, @NotNull Guild guild) {
        this.id = id;
        this.api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Guild getGuild() {
        return guild.resolve();
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @NotNull
    public String getAsMention() {
        return "<#" + id + '>';
    }

    @NotNull
    public JDA getJDA() {
        return api;
    }

    @NotNull
    public MessageAction sendMessage(@NotNull CharSequence text) {
        Checks.notEmpty(text, "Provided text for message");
        Checks.check(text.length() <= 2000, "Provided text for message must be less than 2000 characters in length");

        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(getId());
        if (text instanceof StringBuilder) {
            return new MessageAction(getJDA(), route, this, (StringBuilder) text);
        } else {
            return new MessageAction(getJDA(), route, this).append(text);
        }
    }

    @Override
    public String toString() {
        return "TextChannel:" + getName() + '(' + id + ')';
    }

    @Override
    public int compareTo(@NotNull TextChannel channel) {
        return Long.compareUnsigned(id, channel.getIdLong());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof TextChannel))
            return false;
        TextChannel channel = (TextChannel) obj;
        return channel.getIdLong() == getIdLong();
    }
}
