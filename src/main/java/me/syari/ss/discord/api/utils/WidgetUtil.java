
package me.syari.ss.discord.api.utils;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WidgetUtil 
{
    public static final String WIDGET_PNG = Requester.DISCORD_API_PREFIX + "guilds/%s/widget.png?style=%s";
    public static final String WIDGET_URL = Requester.DISCORD_API_PREFIX + "guilds/%s/widget.json";
    public static final String WIDGET_HTML = "<iframe src=\"https://discordapp.com/widget?id=%s&theme=%s\" width=\"%d\" height=\"%d\" allowtransparency=\"true\" frameborder=\"0\"></iframe>";
    

    @Nonnull
    public static String getWidgetBanner(@Nonnull Guild guild, @Nonnull BannerType type)
    {
        Checks.notNull(guild, "Guild");
        return getWidgetBanner(guild.getId(), type);
    }
    

    @Nonnull
    public static String getWidgetBanner(@Nonnull String guildId, @Nonnull BannerType type)
    {
        Checks.notNull(guildId, "GuildId");
        Checks.notNull(type, "BannerType");
        return String.format(WIDGET_PNG, guildId, type.name().toLowerCase());
    }
    

    @Nonnull
    public static String getPremadeWidgetHtml(@Nonnull Guild guild, @Nonnull WidgetTheme theme, int width, int height)
    {
        Checks.notNull(guild, "Guild");
        return getPremadeWidgetHtml(guild.getId(), theme, width, height);
    }
    

    @Nonnull
    public static String getPremadeWidgetHtml(@Nonnull String guildId, @Nonnull WidgetTheme theme, int width, int height)
    {
        Checks.notNull(guildId, "GuildId");
        Checks.notNull(theme, "WidgetTheme");
        Checks.notNegative(width, "Width");
        Checks.notNegative(height, "Height");
        return String.format(WIDGET_HTML, guildId, theme.name().toLowerCase(), width, height);
    }
    

    @Nullable
    public static Widget getWidget(@Nonnull String guildId) throws RateLimitedException
    {
        return getWidget(MiscUtil.parseSnowflake(guildId));
    }


    @Nullable
    public static Widget getWidget(long guildId) throws RateLimitedException
    {
        Checks.notNull(guildId, "GuildId");

        HttpURLConnection connection;
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                    .url(String.format(WIDGET_URL, guildId))
                    .method("GET", null)
                    .header("user-agent", Requester.USER_AGENT)
                    .header("accept-encoding", "gzip")
                    .build();

        try (Response response = client.newCall(request).execute())
        {
            final int code = response.code();
            InputStream data = IOUtil.getBody(response);

            switch (code)
            {
                case 200: // ok
                {
                    try (InputStream stream = data)
                    {
                        return new Widget(DataObject.fromJson(stream));
                    }
                    catch (IOException e)
                    {
                        throw new UncheckedIOException(e);
                    }
                }
                case 400: // not valid snowflake
                case 404: // guild not found
                    return null;
                case 403: // widget disabled
                    return new Widget(guildId);
                case 429: // ratelimited
                {
                    long retryAfter;
                    try (InputStream stream = data)
                    {
                        retryAfter = DataObject.fromJson(stream).getLong("retry_after");
                    }
                    catch (Exception e)
                    {
                        retryAfter = 0;
                    }
                    throw new RateLimitedException(WIDGET_URL, retryAfter);
                }
                default:
                    throw new IllegalStateException("An unknown status was returned: " + code + " " + response.message());
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
    

    public enum BannerType
    {
        SHIELD, BANNER1, BANNER2, BANNER3, BANNER4
    }
    

    public enum WidgetTheme
    {
        LIGHT, DARK
    }
    
    public static class Widget implements ISnowflake
    {
        private final boolean isAvailable;
        private final long id;
        private final String name;
        private final String invite;
        private final TLongObjectMap<VoiceChannel> channels;
        private final TLongObjectMap<Member> members;
        

        private Widget(long guildId)
        {
            isAvailable = false;
            id = guildId;
            name = null;
            invite = null;
            channels = new TLongObjectHashMap<>();
            members = new TLongObjectHashMap<>();
        }
        

        private Widget(@Nonnull DataObject json)
        {
            String inviteCode = json.getString("instant_invite", null);
            if (inviteCode != null)
                inviteCode = inviteCode.substring(inviteCode.lastIndexOf("/") + 1);
            
            isAvailable = true;
            id = json.getLong("id");
            name = json.getString("name");
            invite = inviteCode;
            channels = MiscUtil.newLongMap();
            members = MiscUtil.newLongMap();
            
            DataArray channelsJson = json.getArray("channels");
            for (int i = 0; i < channelsJson.length(); i++)
            {
                DataObject channel = channelsJson.getObject(i);
                channels.put(channel.getLong("id"), new VoiceChannel(channel, this));
            }
            
            DataArray membersJson = json.getArray("members");
            for (int i = 0; i<membersJson.length(); i++)
            {
                DataObject memberJson = membersJson.getObject(i);
                Member member = new Member(memberJson, this);
                if (!memberJson.isNull("channel_id")) // voice state
                {
                    VoiceChannel channel = channels.get(memberJson.getLong("channel_id"));
                    member.setVoiceState(new VoiceState(channel, 
                            memberJson.getBoolean("mute"), 
                            memberJson.getBoolean("deaf"), 
                            memberJson.getBoolean("suppress"), 
                            memberJson.getBoolean("self_mute"), 
                            memberJson.getBoolean("self_deaf"),
                            member,
                            this));
                    channel.addMember(member);
                }
                members.put(member.getIdLong(), member);
            }
        }
        

        public boolean isAvailable()
        {
            return isAvailable;
        }

        @Override
        public long getIdLong()
        {
            return id;
        }
        

        @Nonnull
        public String getName()
        {
            checkAvailable();

            return name;
        }
        

        @Nullable
        public String getInviteCode()
        {
            checkAvailable();

            return invite;
        }
        

        @Nonnull
        public List<VoiceChannel> getVoiceChannels()
        {
            checkAvailable();

            return Collections.unmodifiableList(new ArrayList<>(channels.valueCollection()));
        }
        

        @Nullable
        public VoiceChannel getVoiceChannelById(String id)
        {
            checkAvailable();

            return channels.get(MiscUtil.parseSnowflake(id));
        }


        @Nullable
        public VoiceChannel getVoiceChannelById(long id)
        {
            checkAvailable();

            return channels.get(id);
        }
        

        @Nonnull
        public List<Member> getMembers()
        {
            checkAvailable();

            return Collections.unmodifiableList(new ArrayList<>(members.valueCollection()));
        }
        

        @Nullable
        public Member getMemberById(String id)
        {
            checkAvailable();

            return members.get(MiscUtil.parseSnowflake(id));
        }


        @Nullable
        public Member getMemberById(long id)
        {
            checkAvailable();

            return members.get(id);
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Widget))
                return false;
            Widget oWidget = (Widget) obj;
            return this == oWidget || this.id == oWidget.getIdLong();
        }
        
        @Override
        public String toString()
        {
            return "W:" + (isAvailable() ? getName() : "") + '(' + id + ')';
        }

        private void checkAvailable()
        {
            if (!isAvailable)
                throw new IllegalStateException("The widget for this Guild is unavailable!");
        }

        public static class Member implements IMentionable
        {
            private final boolean bot;
            private final long id;
            private final String username;
            private final String discriminator;
            private final String avatar;
            private final String nickname;
            private final OnlineStatus status;
            private final Activity game;
            private final Widget widget;
            private VoiceState state;
            
            private Member(@Nonnull DataObject json, @Nonnull Widget widget)
            {
                this.widget = widget;
                this.bot = json.getBoolean("bot");
                this.id = json.getLong("id");
                this.username = json.getString("username");
                this.discriminator = json.getString("discriminator");
                this.avatar = json.getString("avatar", null);
                this.nickname = json.getString("nick", null);
                this.status = OnlineStatus.fromKey(json.getString("status"));
                this.game = json.isNull("game") ? null : EntityBuilder.createActivity(json.getObject("game"));
            }
            
            private void setVoiceState(VoiceState voiceState)
            {
                state = voiceState;
            }
            

            public boolean isBot()
            {
                return bot;
            }
            

            @Nonnull
            public String getName()
            {
                return username;
            }

            @Override
            public long getIdLong()
            {
                return id;
            }

            @Nonnull
            @Override
            public String getAsMention()
            {
                return "<@" + getId() + ">";
            }
            

            @Nonnull
            public String getDiscriminator()
            {
                return discriminator;
            }
            

            @Nullable
            public String getAvatarId()
            {
                return avatar;
            }
            

            @Nullable
            public String getAvatarUrl()
            {
                String avatarId = getAvatarId();
                return avatarId == null ? null : String.format(User.AVATAR_URL, getId(), avatarId, avatarId.startsWith("a_") ? ".gif" : ".png");
            }


            @Nonnull
            public String getDefaultAvatarId()
            {
                return String.valueOf(Integer.parseInt(getDiscriminator()) % 5);
            }


            @Nonnull
            public String getDefaultAvatarUrl()
            {
                return String.format(User.DEFAULT_AVATAR_URL, getDefaultAvatarId());
            }


            @Nonnull
            public String getEffectiveAvatarUrl()
            {
                String avatarUrl = getAvatarUrl();
                return avatarUrl == null ? getDefaultAvatarUrl() : avatarUrl;
            }
            

            @Nullable
            public String getNickname()
            {
                return nickname;
            }
            

            @Nonnull
            public String getEffectiveName()
            {
                return nickname == null ? username : nickname;
            }
            

            @Nonnull
            public OnlineStatus getOnlineStatus()
            {
                return status;
            }
            

            @Nullable
            public Activity getActivity()
            {
                return game;
            }
            

            @Nonnull
            public VoiceState getVoiceState()
            {
                return state == null ? new VoiceState(this, widget) : state;
            }


            @Nonnull
            public Widget getWidget()
            {
                return widget;
            }

            @Override
            public int hashCode() {
                return (widget.getId() + ' ' + id).hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof Member))
                    return false;
                Member oMember = (Member) obj;
                return this == oMember || (this.id == oMember.getIdLong() && this.widget.getIdLong() == oMember.getWidget().getIdLong());
            }
            
            @Override
            public String toString()
            {
                return "W.M:" + getName() + '(' + id + ')';
            }
        }

        public static class VoiceChannel implements ISnowflake
        {
            private final int position;
            private final long id;
            private final String name;
            private final List<Member> members;
            private final Widget widget;
            
            private VoiceChannel(@Nonnull DataObject json, @Nonnull Widget widget)
            {
                this.widget = widget;
                this.position = json.getInt("position");
                this.id = json.getLong("id");
                this.name = json.getString("name");
                this.members = new ArrayList<>();
            }
            
            private void addMember(@Nonnull Member member)
            {
                members.add(member);
            }
            

            public int getPosition()
            {
                return position;
            }

            @Override
            public long getIdLong()
            {
                return id;
            }
            

            @Nonnull
            public String getName()
            {
                return name;
            }
            

            @Nonnull
            public List<Member> getMembers()
            {
                return members;
            }


            @Nonnull
            public Widget getWidget()
            {
                return widget;
            }

            @Override
            public int hashCode() {
                return Long.hashCode(id);
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof VoiceChannel))
                    return false;
                VoiceChannel oVChannel = (VoiceChannel) obj;
                return this == oVChannel || this.id == oVChannel.getIdLong();
            }
            
            @Override
            public String toString()
            {
                return "W.VC:" + getName() + '(' + id + ')';
            }
        }
        
        public static class VoiceState
        {
            private final VoiceChannel channel;
            private final boolean muted;
            private final boolean deafened;
            private final boolean suppress;
            private final boolean selfMute;
            private final boolean selfDeaf;
            private final Member member;
            private final Widget widget;
            
            private VoiceState(@Nonnull Member member, @Nonnull Widget widget)
            {
                this(null, false, false, false, false, false, member, widget);
            }
            
            private VoiceState(@Nullable VoiceChannel channel, boolean muted, boolean deafened, boolean suppress, boolean selfMute, boolean selfDeaf, @Nonnull Member member, @Nonnull Widget widget)
            {
                this.channel = channel;
                this.muted = muted;
                this.deafened = deafened;
                this.suppress = suppress;
                this.selfMute = selfMute;
                this.selfDeaf = selfDeaf;
                this.member = member;
                this.widget = widget;
            }
            

            @Nullable
            public VoiceChannel getChannel()
            {
                return channel;
            }
            

            public boolean inVoiceChannel()
            {
                return channel != null;
            }
            

            public boolean isGuildMuted()
            {
                return muted;
            }
            

            public boolean isGuildDeafened()
            {
                return deafened;
            }
            

            public boolean isSuppressed()
            {
                return suppress;
            }
            

            public boolean isSelfMuted()
            {
                return selfMute;
            }
            

            public boolean isSelfDeafened()
            {
                return selfDeaf;
            }
            

            public boolean isMuted()
            {
                return selfMute || muted;
            }
            

            public boolean isDeafened()
            {
                return selfDeaf || deafened;
            }

            @Nonnull
            public Member getMember()
            {
                return member;
            }

            @Nonnull
            public Widget getWidget()
            {
                return widget;
            }

            @Override
            public int hashCode() {
                return member.hashCode();
            }
            
            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof VoiceState))
                    return false;
                VoiceState oState = (VoiceState) obj;
                return this == oState || (this.member.equals(oState.getMember()) && this.widget.equals(oState.getWidget()));
            }
            
            @Override
            public String toString() {
                return "VS:" + widget.getName() + ':' + member.getEffectiveName();
            }
        }
    }
}
