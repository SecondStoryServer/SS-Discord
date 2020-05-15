
package me.syari.ss.discord.api;

import me.syari.ss.discord.api.entities.EmbedType;
import me.syari.ss.discord.api.entities.MessageEmbed;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class EmbedBuilder
{
    public final static String ZERO_WIDTH_SPACE = "\u200E";
    public final static Pattern URL_PATTERN = Pattern.compile("\\s*(https?|attachment)://\\S+\\s*", Pattern.CASE_INSENSITIVE);

    private final List<MessageEmbed.Field> fields = new LinkedList<>();
    private final StringBuilder description = new StringBuilder();
    private int color = Role.DEFAULT_COLOR_RAW;
    private String url, title;
    private OffsetDateTime timestamp;
    private MessageEmbed.Thumbnail thumbnail;
    private MessageEmbed.AuthorInfo author;
    private MessageEmbed.Footer footer;
    private MessageEmbed.ImageInfo image;

    
    public EmbedBuilder() { }

    public EmbedBuilder(@Nullable EmbedBuilder builder)
    {
        if (builder != null)
        {
            setDescription(builder.description.toString());
            this.fields.addAll(builder.fields);
            this.url = builder.url;
            this.title = builder.title;
            this.timestamp = builder.timestamp;
            this.color = builder.color;
            this.thumbnail = builder.thumbnail;
            this.author = builder.author;
            this.footer = builder.footer;
            this.image = builder.image;
        }
    }
    
    
    public EmbedBuilder(@Nullable MessageEmbed embed)
    {
        if(embed != null)
        {
            setDescription(embed.getDescription());
            this.url = embed.getUrl();
            this.title = embed.getTitle();
            this.timestamp = embed.getTimestamp();
            this.color = embed.getColorRaw();
            this.thumbnail = embed.getThumbnail();
            this.author = embed.getAuthor();
            this.footer = embed.getFooter();
            this.image = embed.getImage();
            if (embed.getFields() != null)
                fields.addAll(embed.getFields());
        }
    }

    
    @Nonnull
    public MessageEmbed build()
    {
        if (isEmpty())
            throw new IllegalStateException("Cannot build an empty embed!");
        if (description.length() > MessageEmbed.TEXT_MAX_LENGTH)
            throw new IllegalStateException(String.format("Description is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
        if (length() > MessageEmbed.EMBED_MAX_LENGTH_BOT)
            throw new IllegalStateException("Cannot build an embed with more than " + MessageEmbed.EMBED_MAX_LENGTH_BOT + " characters!");
        final String description = this.description.length() < 1 ? null : this.description.toString();

        return EntityBuilder.createMessageEmbed(url, title, description, EmbedType.RICH, timestamp,
                color, thumbnail, null, author, null, footer, image, new LinkedList<>(fields));
    }

    
    @Nonnull
    public EmbedBuilder clear()
    {
        description.setLength(0);
        fields.clear();
        url = null;
        title = null;
        timestamp = null;
        color = Role.DEFAULT_COLOR_RAW;
        thumbnail = null;
        author = null;
        footer = null;
        image = null;
        return this;
    }

    
    public boolean isEmpty()
    {
        return title == null
            && timestamp == null
            && thumbnail == null
            && author == null
            && footer == null
            && image == null
            && color == Role.DEFAULT_COLOR_RAW
            && description.length() == 0
            && fields.isEmpty();
    }

    
    public int length()
    {
        int length = description.length();
        synchronized (fields)
        {
            length = fields.stream().map(f -> f.getName().length() + f.getValue().length()).reduce(length, Integer::sum);
        }
        if (title != null)
            length += title.length();
        if (author != null)
            length += author.getName().length();
        if (footer != null)
            length += footer.getText().length();
        return length;
    }

    
    public boolean isValidLength(@Nonnull AccountType type)
    {
        Checks.notNull(type, "AccountType");
        final int length = length();
        switch (type)
        {
            case BOT:
                return length <= MessageEmbed.EMBED_MAX_LENGTH_BOT;
            case CLIENT:
            default:
                return length <= MessageEmbed.EMBED_MAX_LENGTH_CLIENT;
        }
    }

    
    @Nonnull
    public EmbedBuilder setTitle(@Nullable String title)
    {
        return setTitle(title, null);
    }
    
    
    @Nonnull
    public EmbedBuilder setTitle(@Nullable String title, @Nullable String url)
    {
        if (title == null)
        {
            this.title = null;
            this.url = null;
        }
        else
        {
            Checks.notEmpty(title, "Title");
            Checks.check(title.length() <= MessageEmbed.TITLE_MAX_LENGTH, "Title cannot be longer than %d characters.", MessageEmbed.TITLE_MAX_LENGTH);
            if (Helpers.isBlank(url))
                url = null;
            urlCheck(url);

            this.title = title;
            this.url = url;
        }
        return this;
    }

    
    @Nonnull
    public StringBuilder getDescriptionBuilder()
    {
        return description;
    }

    
    @Nonnull
    public final EmbedBuilder setDescription(@Nullable CharSequence description)
    {
        this.description.setLength(0);
        if (description != null && description.length() >= 1)
            appendDescription(description);
        return this;
    }

    
    @Nonnull
    public EmbedBuilder appendDescription(@Nonnull CharSequence description)
    {
        Checks.notNull(description, "description");
        Checks.check(this.description.length() + description.length() <= MessageEmbed.TEXT_MAX_LENGTH,
                "Description cannot be longer than %d characters.", MessageEmbed.TEXT_MAX_LENGTH);
        this.description.append(description);
        return this;
    }

    
    @Nonnull
    public EmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal)
    {
        if (temporal == null)
        {
            this.timestamp = null;
        }
        else if (temporal instanceof OffsetDateTime)
        {
            this.timestamp = (OffsetDateTime) temporal;
        }
        else
        {
            ZoneOffset offset;
            try
            {
                offset = ZoneOffset.from(temporal);
            }
            catch (DateTimeException ignore)
            {
                offset = ZoneOffset.UTC;
            }
            try
            {
                LocalDateTime ldt = LocalDateTime.from(temporal);
                this.timestamp = OffsetDateTime.of(ldt, offset);
            }
            catch (DateTimeException ignore)
            {
                try
                {
                    Instant instant = Instant.from(temporal);
                    this.timestamp = OffsetDateTime.ofInstant(instant, offset);
                }
                catch (DateTimeException ex)
                {
                    throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " +
                            temporal + " of type " + temporal.getClass().getName(), ex);
                }
            }
        }
        return this; 
    }
    
    
    @Nonnull
    public EmbedBuilder setColor(@Nullable Color color)
    {
        this.color = color == null ? Role.DEFAULT_COLOR_RAW : color.getRGB();
        return this;
    }

    
    @Nonnull
    public EmbedBuilder setColor(int color)
    {
        this.color = color;
        return this;
    }
    
    
    @Nonnull
    public EmbedBuilder setThumbnail(@Nullable String url)
    {
        if (url == null)
        {
            this.thumbnail = null;
        }
        else
        {
            urlCheck(url);
            this.thumbnail = new MessageEmbed.Thumbnail(url, null, 0, 0);
        }
        return this;
    }

    
    @Nonnull
    public EmbedBuilder setImage(@Nullable String url)
    {
        if (url == null)
        {
            this.image = null;
        }
        else
        {
            urlCheck(url);
            this.image = new MessageEmbed.ImageInfo(url, null, 0, 0);
        }
        return this;
    }
    
    
    @Nonnull
    public EmbedBuilder setAuthor(@Nullable String name)
    {
        return setAuthor(name, null, null);
    }

    
    @Nonnull
    public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url)
    {
        return setAuthor(name, url, null);
    }

    
    @Nonnull
    public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl)
    {
        //We only check if the name is null because its presence is what determines if the
        // the author will appear in the embed.
        if (name == null)
        {
            this.author = null;
        }
        else
        {
            urlCheck(url);
            urlCheck(iconUrl);
            this.author = new MessageEmbed.AuthorInfo(name, url, iconUrl, null);
        }
        return this;
    }

    
    @Nonnull
    public EmbedBuilder setFooter(@Nullable String text)
    {
        return setFooter(text, null);
    }

    
    @Nonnull
    public EmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl)
    {
        //We only check if the text is null because its presence is what determines if the
        // footer will appear in the embed.
        if (text == null)
        {
            this.footer = null;
        }
        else
        {
            Checks.check(text.length() <= MessageEmbed.TEXT_MAX_LENGTH, "Text cannot be longer than %d characters.", MessageEmbed.TEXT_MAX_LENGTH);
            urlCheck(iconUrl);
            this.footer = new MessageEmbed.Footer(text, iconUrl, null);
        }
        return this;
    }

    
    @Nonnull
    public EmbedBuilder addField(@Nullable MessageEmbed.Field field)
    {
        return field == null ? this : addField(field.getName(), field.getValue(), field.isInline());
    }
    
    
    @Nonnull
    public EmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline)
    {
        if (name == null && value == null)
            return this;
        this.fields.add(new MessageEmbed.Field(name, value, inline));
        return this;
    }
    
    
    @Nonnull
    public EmbedBuilder addBlankField(boolean inline)
    {
        this.fields.add(new MessageEmbed.Field(ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, inline));
        return this;
    }

    
    @Nonnull
    public EmbedBuilder clearFields()
    {
        this.fields.clear();
        return this;
    }
    
    
    @Nonnull
    public List<MessageEmbed.Field> getFields()
    {
        return fields;
    }

    private void urlCheck(@Nullable String url)
    {
        if (url != null)
        {
            Checks.check(url.length() <= MessageEmbed.URL_MAX_LENGTH, "URL cannot be longer than %d characters.", MessageEmbed.URL_MAX_LENGTH);
            Checks.check(URL_PATTERN.matcher(url).matches(), "URL must be a valid http(s) or attachment url.");
        }
    }
}
