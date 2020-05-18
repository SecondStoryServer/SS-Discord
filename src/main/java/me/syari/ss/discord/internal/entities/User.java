package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.internal.JDA;
import org.jetbrains.annotations.NotNull;

public class User implements ISnowflake {
    protected final long id;
    protected final JDA api;

    protected short discriminator;
    protected String name;
    protected boolean bot;
    protected boolean fake = false;

    public User(long id, JDA api) {
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

    public boolean isBot() {
        return bot;
    }

    @NotNull
    public JDA getJDA() {
        return api;
    }

    @Override
    public long getIdLong() {
        return id;
    }

    public boolean isFake() {
        return fake;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof User)) {
            return false;
        }
        User user = (User) object;
        return this.id == user.id;
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
}
