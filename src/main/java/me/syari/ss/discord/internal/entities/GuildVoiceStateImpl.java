package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildVoiceState;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference;

import javax.annotation.Nonnull;

public class GuildVoiceStateImpl implements GuildVoiceState {
    private final SnowflakeReference<Guild> guild;
    private final SnowflakeReference<Member> member;
    private final JDA api;

    private VoiceChannel connectedChannel;
    private boolean selfMuted = false;
    private boolean selfDeafened = false;
    private boolean guildMuted = false;
    private boolean guildDeafened = false;
    private boolean suppressed = false;

    public GuildVoiceStateImpl(Member member) {
        this.api = member.getJDA();
        this.guild = new SnowflakeReference<>(member.getGuild(), api::getGuildById);
        this.member = new SnowflakeReference<>(member, (id) -> guild.resolve().getMemberById(id));
    }

    @Override
    public boolean isSelfMuted() {
        return selfMuted;
    }

    @Override
    public boolean isSelfDeafened() {
        return selfDeafened;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return api;
    }

    @Override
    public boolean isMuted() {
        return isSelfMuted() || isGuildMuted();
    }

    @Override
    public boolean isDeafened() {
        return isSelfDeafened() || isGuildDeafened();
    }

    @Override
    public boolean isGuildMuted() {
        return guildMuted;
    }

    @Override
    public boolean isGuildDeafened() {
        return guildDeafened;
    }

    @Override
    public boolean isSuppressed() {
        return suppressed;
    }

    @Override
    public VoiceChannel getChannel() {
        return connectedChannel;
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return this.guild.resolve();
    }

    @Nonnull
    @Override
    public Member getMember() {
        return this.member.resolve();
    }

    @Override
    public int hashCode() {
        return getMember().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GuildVoiceState))
            return false;
        GuildVoiceState oStatus = (GuildVoiceState) obj;
        return this.getMember().equals(oStatus.getMember());
    }

    @Override
    public String toString() {
        return "VS:" + getGuild().getName() + ':' + getMember().getEffectiveName();
    }

    // -- Setters --

    public GuildVoiceStateImpl setConnectedChannel(VoiceChannel connectedChannel) {
        this.connectedChannel = connectedChannel;
        return this;
    }

    public GuildVoiceStateImpl setSessionId(String sessionId) {
        return this;
    }

    public GuildVoiceStateImpl setSelfMuted(boolean selfMuted) {
        this.selfMuted = selfMuted;
        return this;
    }

    public GuildVoiceStateImpl setSelfDeafened(boolean selfDeafened) {
        this.selfDeafened = selfDeafened;
        return this;
    }

    public GuildVoiceStateImpl setGuildMuted(boolean guildMuted) {
        this.guildMuted = guildMuted;
        return this;
    }

    public GuildVoiceStateImpl setGuildDeafened(boolean guildDeafened) {
        this.guildDeafened = guildDeafened;
        return this;
    }

    public GuildVoiceStateImpl setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
        return this;
    }
}
