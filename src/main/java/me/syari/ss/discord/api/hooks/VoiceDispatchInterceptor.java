

package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildVoiceState;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.managers.DirectAudioController;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.api.utils.data.SerializableData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface VoiceDispatchInterceptor
{

    void onVoiceServerUpdate(@Nonnull VoiceServerUpdate update);


    boolean onVoiceStateUpdate(@Nonnull VoiceStateUpdate update);


    interface VoiceUpdate extends SerializableData
    {

        @Nonnull
        Guild getGuild();


        @Nonnull
        @Override
        DataObject toData();


        @Nonnull
        default DirectAudioController getAudioController()
        {
            return getJDA().getDirectAudioController();
        }


        default long getGuildIdLong()
        {
            return getGuild().getIdLong();
        }


        @Nonnull
        default String getGuildId()
        {
            return Long.toUnsignedString(getGuildIdLong());
        }


        @Nonnull
        default JDA getJDA()
        {
            return getGuild().getJDA();
        }


        @Nullable
        default JDA.ShardInfo getShardInfo()
        {
            return getJDA().getShardInfo();
        }
    }


    class VoiceServerUpdate implements VoiceUpdate
    {
        private final Guild guild;
        private final String endpoint;
        private final String token;
        private final String sessionId;
        private final DataObject json;

        public VoiceServerUpdate(Guild guild, String endpoint, String token, String sessionId, DataObject json)
        {
            this.guild = guild;
            this.endpoint = endpoint;
            this.token = token;
            this.sessionId = sessionId;
            this.json = json;
        }

        @Nonnull
        @Override
        public Guild getGuild()
        {
            return guild;
        }

        @Nonnull
        @Override
        public DataObject toData()
        {
            return json;
        }


        @Nonnull
        public String getEndpoint()
        {
            return endpoint;
        }


        @Nonnull
        public String getToken()
        {
            return token;
        }


        @Nonnull
        public String getSessionId()
        {
            return sessionId;
        }
    }


    class VoiceStateUpdate implements VoiceUpdate
    {
        private final VoiceChannel channel;
        private final GuildVoiceState voiceState;
        private final DataObject json;

        public VoiceStateUpdate(VoiceChannel channel, GuildVoiceState voiceState, DataObject json)
        {
            this.channel = channel;
            this.voiceState = voiceState;
            this.json = json;
        }

        @Nonnull
        @Override
        public Guild getGuild()
        {
            return voiceState.getGuild();
        }

        @Nonnull
        @Override
        public DataObject toData()
        {
            return json;
        }


        @Nullable
        public VoiceChannel getChannel()
        {
            return channel;
        }


        @Nonnull
        public GuildVoiceState getVoiceState()
        {
            return voiceState;
        }
    }
}
