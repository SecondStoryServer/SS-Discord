
package me.syari.ss.discord.api;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.internal.entities.DataMessage;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.MessageActionImpl;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;


public class MessageBuilder implements Appendable
{
    protected final StringBuilder builder = new StringBuilder();

    protected boolean isTTS = false;
    protected String nonce;
    protected MessageEmbed embed;

    public MessageBuilder() {}

    public MessageBuilder(@Nullable CharSequence content)
    {
        if (content != null)
            builder.append(content);
    }

    public MessageBuilder(@Nullable Message message)
    {
        if (message != null)
        {
            isTTS = message.isTTS();
            builder.append(message.getContentRaw());
            List<MessageEmbed> embeds = message.getEmbeds();
            if (embeds != null && !embeds.isEmpty())
                embed = embeds.get(0);
        }
    }

    public MessageBuilder(@Nullable MessageBuilder builder)
    {
        if (builder != null)
        {
            this.isTTS = builder.isTTS;
            this.builder.append(builder.builder);
            this.nonce = builder.nonce;
            this.embed = builder.embed;
        }
    }

    public MessageBuilder(@Nullable EmbedBuilder builder)
    {
        if (builder != null)
            this.embed = builder.build();
    }

    public MessageBuilder(@Nullable MessageEmbed embed)
    {
        this.embed = embed;
    }


    @Nonnull
    public MessageBuilder setTTS(boolean tts)
    {
        this.isTTS = tts;
        return this;
    }
    

    @Nonnull
    public MessageBuilder setEmbed(@Nullable MessageEmbed embed)
    {
        this.embed = embed;
        return this;
    }


    @Nonnull
    public MessageBuilder setNonce(@Nullable String nonce)
    {
        this.nonce = nonce;
        return this;
    }


    @Nonnull
    public MessageBuilder setContent(@Nullable String content)
    {
        if (content == null)
        {
            builder.setLength(0);
        }
        else
        {
            final int newLength = Math.max(builder.length(), content.length());
            builder.replace(0, newLength, content);
        }
        return this;
    }

    @Nonnull
    @Override
    public MessageBuilder append(@Nullable CharSequence text)
    {
        builder.append(text);
        return this;
    }

    @Nonnull
    @Override
    public MessageBuilder append(@Nullable CharSequence text, int start, int end)
    {
        builder.append(text, start, end);
        return this;
    }

    @Nonnull
    @Override
    public MessageBuilder append(char c)
    {
        builder.append(c);
        return this;
    }


    @Nonnull
    public MessageBuilder append(@Nullable Object object)
    {
        return append(String.valueOf(object));
    }


    @Nonnull
    public MessageBuilder append(@Nonnull IMentionable mention)
    {
        Checks.notNull(mention, "Mentionable");
        builder.append(mention.getAsMention());
        return this;
    }


    @Nonnull
    public MessageBuilder append(@Nullable CharSequence text, @Nonnull Formatting... format)
    {
        boolean blockPresent = false;
        for (Formatting formatting : format)
        {
            if (formatting == Formatting.BLOCK)
            {
                blockPresent = true;
                continue;
            }
            builder.append(formatting.getTag());
        }
        if (blockPresent)
            builder.append(Formatting.BLOCK.getTag());

        builder.append(text);

        if (blockPresent)
            builder.append(Formatting.BLOCK.getTag());
        for (int i = format.length - 1; i >= 0; i--)
        {
            if (format[i] == Formatting.BLOCK) continue;
            builder.append(format[i].getTag());
        }
        return this;
    }


    @Nonnull
    public MessageBuilder appendFormat(@Nonnull String format, @Nonnull Object... args)
    {
        Checks.notEmpty(format, "Format String");
        this.append(String.format(format, args));
        return this;
    }
    

    @Nonnull
    public MessageBuilder appendCodeLine(@Nullable CharSequence text)
    {
        this.append(text, Formatting.BLOCK);
        return this;
    }


    @Nonnull
    public MessageBuilder appendCodeBlock(@Nullable CharSequence text, @Nullable CharSequence language)
    {
        builder.append("```").append(language).append('\n').append(text).append("\n```");
        return this;
    }


    public int length()
    {
        return builder.length();
    }


    public boolean isEmpty() {
        return builder.length() == 0 && embed == null;
    }


    @Nonnull
    public MessageBuilder replace(@Nonnull String target, @Nonnull String replacement)
    {
        int index = builder.indexOf(target);
        while (index != -1)
        {
            builder.replace(index, index + target.length(), replacement);
            index = builder.indexOf(target, index + replacement.length());
        }
        return this;
    }


    @Nonnull
    public MessageBuilder replaceFirst(@Nonnull String target, @Nonnull String replacement)
    {
        int index = builder.indexOf(target);
        if (index != -1)
        {
            builder.replace(index, index + target.length(), replacement);
        }
        return this;
    }


    @Nonnull
    public MessageBuilder replaceLast(@Nonnull String target, @Nonnull String replacement)
    {
        int index = builder.lastIndexOf(target);
        if (index != -1)
        {
            builder.replace(index, index + target.length(), replacement);
        }
        return this;
    }


    @Nonnull
    public MessageBuilder stripMentions(@Nonnull JDA jda)
    {
        // Note: Users can rename to "everyone" or "here", so those
        // should be stripped after the USER mention is stripped.
        return this.stripMentions(jda, null, Message.MentionType.values());
    }


    @Nonnull
    public MessageBuilder stripMentions(@Nonnull Guild guild)
    {
        // Note: Users can rename to "everyone" or "here", so those
        // should be stripped after the USER mention is stripped.
        return this.stripMentions(guild.getJDA(), guild, Message.MentionType.values());
    }


    @Nonnull
    public MessageBuilder stripMentions(@Nonnull Guild guild, @Nonnull Message.MentionType... types)
    {
        return this.stripMentions(guild.getJDA(), guild, types);
    }


    @Nonnull
    public MessageBuilder stripMentions(@Nonnull JDA jda, @Nonnull Message.MentionType... types)
    {
        return this.stripMentions(jda, null, types);
    }

    @Nonnull
    private MessageBuilder stripMentions(JDA jda, Guild guild, Message.MentionType... types)
    {
        if (types == null)
            return this;

        String string = null;

        for (Message.MentionType mention : types)
        {
            if (mention != null)
            {
                switch (mention)
                {
                    case EVERYONE:
                        replace("@everyone", "@\u0435veryone");
                        break;
                    case HERE:
                        replace("@here", "@h\u0435re");
                        break;
                    case CHANNEL:
                    {
                        if (string == null)
                        {
                            string = builder.toString();
                        }

                        Matcher matcher = Message.MentionType.CHANNEL.getPattern().matcher(string);
                        while (matcher.find())
                        {
                            TextChannel channel = jda.getTextChannelById(matcher.group(1));
                            if (channel != null)
                            {
                                replace(matcher.group(), "#" + channel.getName());
                            }
                        }
                        break;
                    }
                    case ROLE:
                    {
                        if (string == null)
                        {
                            string = builder.toString();
                        }

                        Matcher matcher = Message.MentionType.ROLE.getPattern().matcher(string);
                        while (matcher.find())
                        {
                            for (Guild g : jda.getGuilds())
                            {
                                Role role = g.getRoleById(matcher.group(1));
                                if (role != null)
                                {
                                    replace(matcher.group(), "@"+role.getName());
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case USER:
                    {
                        if (string == null)
                        {
                            string = builder.toString();
                        }

                        Matcher matcher = Message.MentionType.USER.getPattern().matcher(string);
                        while (matcher.find())
                        {
                            User user = jda.getUserById(matcher.group(1));
                            String replacement;

                            if (user == null)
                                continue;

                            Member member;

                            if (guild != null && (member = guild.getMember(user)) != null)
                                replacement = member.getEffectiveName();
                            else
                                replacement = user.getName();

                            replace(matcher.group(), "@" + replacement);
                        }
                        break;
                    }
                }
            }
        }

        return this;
    }


    @Nonnull
    public StringBuilder getStringBuilder()
    {
        return this.builder;
    }


    @Nonnull
    public MessageBuilder clear() {
        this.builder.setLength(0);
        this.embed = null;
        this.isTTS = false;
        return this;
    }


    public int indexOf(@Nonnull CharSequence target, int fromIndex, int endIndex)
    {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + fromIndex);
        if (endIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + endIndex);
        if (fromIndex > length())
            throw new IndexOutOfBoundsException("fromIndex > length()");
        if (fromIndex > endIndex)
            throw new IndexOutOfBoundsException("fromIndex > endIndex");

        if (endIndex >= builder.length())
        {
            endIndex = builder.length() - 1;
        }

        int targetCount = target.length();
        if (targetCount == 0)
        {
            return fromIndex;
        }

        char strFirstChar = target.charAt(0);
        int max = endIndex + targetCount - 1;

        lastCharSearch:
        for (int i = fromIndex; i <= max; i++)
        {
            if (builder.charAt(i) == strFirstChar)
            {
                for (int j = 1; j < targetCount; j++)
                {
                    if (builder.charAt(i + j) != target.charAt(j))
                    {
                        continue lastCharSearch;
                    }
                }
                return i;
            }
        }
        return -1;
    }


    public int lastIndexOf(@Nonnull CharSequence target, int fromIndex, int endIndex)
    {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + fromIndex);
        if (endIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + endIndex);
        if (fromIndex > length())
            throw new IndexOutOfBoundsException("fromIndex > length()");
        if (fromIndex > endIndex)
            throw new IndexOutOfBoundsException("fromIndex > endIndex");

        if (endIndex >= builder.length())
        {
            endIndex = builder.length() - 1;
        }

        int targetCount = target.length();
        if (targetCount == 0)
        {
            return endIndex;
        }

        int rightIndex = endIndex - targetCount;

        if (fromIndex > rightIndex)
        {
            fromIndex = rightIndex;
        }

        int strLastIndex = targetCount - 1;
        char strLastChar = target.charAt(strLastIndex);

        int min = fromIndex + targetCount - 1;

        lastCharSearch:
        for (int i = endIndex; i >= min; i--)
        {
            if (builder.charAt(i) == strLastChar)
            {
                for (int j = strLastIndex - 1, k = 1; j >= 0; j--, k++)
                {
                    if (builder.charAt(i - k) != target.charAt(j))
                    {
                        continue lastCharSearch;
                    }
                }
                return i - target.length() + 1;
            }
        }
        return -1;
    }


    @Nonnull
    @CheckReturnValue
    public MessageAction sendTo(@Nonnull MessageChannel channel)
    {
        Checks.notNull(channel, "Target Channel");
        switch (channel.getType())
        {
            case TEXT:
                final TextChannel text = (TextChannel) channel;
                final Member self = text.getGuild().getSelfMember();
                if (!self.hasPermission(text, Permission.MESSAGE_READ))
                    throw new InsufficientPermissionException(text, Permission.MESSAGE_READ);
                if (!self.hasPermission(text, Permission.MESSAGE_WRITE))
                    throw new InsufficientPermissionException(text, Permission.MESSAGE_WRITE);
                break;
            case PRIVATE:
                final PrivateChannel priv = (PrivateChannel) channel;
                if (priv.getUser().isBot() && channel.getJDA().getAccountType() == AccountType.BOT)
                    throw new UnsupportedOperationException("Cannot send a private message between bots.");
        }
        final Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(channel.getId());
        final MessageActionImpl action = new MessageActionImpl(channel.getJDA(), route, channel, builder);
        return action.tts(isTTS).embed(embed).nonce(nonce);
    }


    @Nonnull
    public Message build()
    {
        String message = builder.toString();
        if (this.isEmpty())
            throw new IllegalStateException("Cannot build a Message with no content. (You never added any content to the message)");
        if (message.length() > Message.MAX_CONTENT_LENGTH)
            throw new IllegalStateException("Cannot build a Message with more than 2000 characters. Please limit your input.");

        return new DataMessage(isTTS, message, nonce, embed);
    }


    @Nonnull
    public Queue<Message> buildAll(@Nullable SplitPolicy... policy)
    {
        if (this.isEmpty())
            throw new UnsupportedOperationException("Cannot build a Message with no content. (You never added any content to the message)");

        LinkedList<Message> messages = new LinkedList<>();

        if (builder.length() <= 2000) {
            messages.add(this.build());
            return messages;
        }

        if (policy == null || policy.length == 0)
        {
            policy = new SplitPolicy[]{ SplitPolicy.ANYWHERE };
        }

        int currentBeginIndex = 0;

        messageLoop:
        while (currentBeginIndex < builder.length() - 2001)
        {
            for (int i = 0; i < policy.length; i++)
            {
                int currentEndIndex = policy[i].nextMessage(currentBeginIndex, this);
                if (currentEndIndex != -1)
                {
                    messages.add(build(currentBeginIndex, currentEndIndex));
                    currentBeginIndex = currentEndIndex;
                    continue messageLoop;
                }
            }
            throw new IllegalStateException("Failed to split the messages");
        }

        if (currentBeginIndex < builder.length() - 1)
        {
            messages.add(build(currentBeginIndex, builder.length() - 1));
        }

        if (this.embed != null)
        {
            ((DataMessage) messages.get(messages.size() - 1)).setEmbed(embed);
        }

        return messages;
    }

    @Nonnull
    protected DataMessage build(int beginIndex, int endIndex)
    {
        return new DataMessage(isTTS, builder.substring(beginIndex, endIndex), null, null);
    }


    public interface SplitPolicy
    {

        SplitPolicy NEWLINE = new CharSequenceSplitPolicy("\n", true);


        SplitPolicy SPACE = new CharSequenceSplitPolicy(" ", true);


        SplitPolicy ANYWHERE = (i, b) -> Math.min(i + 2000, b.length());


        @Nonnull
        static SplitPolicy onChars(@Nonnull CharSequence chars, boolean remove)
        {
            return new CharSequenceSplitPolicy(chars, remove);
        }


        class CharSequenceSplitPolicy implements SplitPolicy
        {
            private final boolean remove;
            private final CharSequence chars;

            private CharSequenceSplitPolicy(@Nonnull final CharSequence chars, final boolean remove)
            {
                this.chars = chars;
                this.remove = remove;
            }

            @Override
            public int nextMessage(final int currentBeginIndex, final MessageBuilder builder)
            {
                int currentEndIndex = builder.lastIndexOf(this.chars, currentBeginIndex, currentBeginIndex + 2000 - (this.remove ? this.chars.length() : 0));
                if (currentEndIndex < 0)
                {
                    return -1;
                }
                else
                {
                    return currentEndIndex + this.chars.length();
                }
            }
        }


        int nextMessage(int currentBeginIndex, MessageBuilder builder);
    }


    public enum Formatting
    {
        ITALICS("*"),
        BOLD("**"),
        STRIKETHROUGH("~~"),
        UNDERLINE("__"),
        BLOCK("`");

        private final String tag;

        Formatting(String tag)
        {
            this.tag = tag;
        }

        @Nonnull
        private String getTag()
        {
            return tag;
        }
    }
}
