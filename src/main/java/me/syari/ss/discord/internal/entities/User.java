package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.Mentionable;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

import java.util.FormattableFlags;
import java.util.Formatter;

public class User implements Mentionable {
    protected final long id;
    protected final JDAImpl api;

    protected short discriminator;
    protected String name;
    protected boolean bot;
    protected boolean fake = false;

    public User(long id, JDAImpl api) {
        this.id = id;
        this.api = api;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDiscriminator() {
        return String.format("%04d", discriminator);
    }

    @NotNull
    private String getAsTag() {
        return getName() + '#' + getDiscriminator();
    }

    public boolean isBot() {
        return bot;
    }

    @NotNull
    public JDAImpl getJDA() {
        return api;
    }

    @NotNull
    @Override
    public String getAsMention() {
        return "<@" + getId() + '>';
    }

    @Override
    public long getIdLong() {
        return id;
    }

    public boolean isFake() {
        return fake;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User))
            return false;
        User oUser = (User) o;
        return this.id == oUser.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "U:" + getName() + '(' + id + ')';
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setDiscriminator(String discriminator) {
        this.discriminator = Short.parseShort(discriminator);
        return this;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public User setFake(boolean fake) {
        this.fake = fake;
        return this;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;

        String out;
        if (!alt)
            out = getAsMention();
        else if (upper)
            out = getAsTag().toUpperCase();
        else
            out = getAsTag();

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
