
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.EmbedBuilder;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.api.utils.data.SerializableData;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MessageEmbed implements SerializableData
{

    public static final int TITLE_MAX_LENGTH = 256;


    public static final int VALUE_MAX_LENGTH = 1024;


    public static final int TEXT_MAX_LENGTH = 2048;


    public static final int URL_MAX_LENGTH = 2000;


    public static final int EMBED_MAX_LENGTH_BOT = 6000;


    public static final int EMBED_MAX_LENGTH_CLIENT = 2000;

    protected final Object mutex = new Object();

    protected final String url;
    protected final String title;
    protected final String description;
    protected final EmbedType type;
    protected final OffsetDateTime timestamp;
    protected final int color;
    protected final Thumbnail thumbnail;
    protected final Provider siteProvider;
    protected final AuthorInfo author;
    protected final VideoInfo videoInfo;
    protected final Footer footer;
    protected final ImageInfo image;
    protected final List<Field> fields;

    protected volatile int length = -1;
    protected volatile DataObject json = null;

    public MessageEmbed(
        String url, String title, String description, EmbedType type, OffsetDateTime timestamp,
        int color, Thumbnail thumbnail, Provider siteProvider, AuthorInfo author,
        VideoInfo videoInfo, Footer footer, ImageInfo image, List<Field> fields)
    {
        this.url = url;
        this.title = title;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
        this.color = color;
        this.thumbnail = thumbnail;
        this.siteProvider = siteProvider;
        this.author = author;
        this.videoInfo = videoInfo;
        this.footer = footer;
        this.image = image;
        this.fields = fields != null && !fields.isEmpty()
            ? Collections.unmodifiableList(fields) : Collections.emptyList();
    }


    @Nullable
    public String getUrl()
    {
        return url;
    }


    @Nullable
    public String getTitle()
    {
        return title;
    }


    @Nullable
    public String getDescription()
    {
        return description;
    }


    @Nonnull
    public EmbedType getType()
    {
        return type;
    }


    @Nullable
    public Thumbnail getThumbnail()
    {
        return thumbnail;
    }


    @Nullable
    public Provider getSiteProvider()
    {
        return siteProvider;
    }


    @Nullable
    public AuthorInfo getAuthor()
    {
        return author;
    }


    @Nullable
    public VideoInfo getVideoInfo()
    {
        return videoInfo;
    }
    

    @Nullable
    public Footer getFooter()
    {
        return footer;
    }
    

    @Nullable
    public ImageInfo getImage()
    {
        return image;
    }
    

    @Nonnull
    public List<Field> getFields()
    {
        return fields;
    }
    

    @Nullable
    public Color getColor()
    {
        return color != Role.DEFAULT_COLOR_RAW ? new Color(color) : null;
    }


    public int getColorRaw()
    {
        return color;
    }
    

    @Nullable
    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }


    public boolean isEmpty()
    {
        return color == Role.DEFAULT_COLOR_RAW
            && timestamp == null
            && getImage() == null
            && getThumbnail() == null
            && getLength() == 0;
    }


    public int getLength()
    {
        if (length > -1)
            return length;
        synchronized (mutex)
        {
            if (length > -1)
                return length;
            length = 0;

            if (title != null)
                length += title.length();
            if (description != null)
                length += description.length();
            if (author != null)
                length += author.getName().length();
            if (footer != null)
                length += footer.getText().length();
            if (fields != null)
            {
                for (Field f : fields)
                    length += f.getName().length() + f.getValue().length();
            }

            return length;
        }
    }


    public boolean isSendable(@Nonnull AccountType type)
    {
        Checks.notNull(type, "AccountType");
        final int length = getLength();
        if (isEmpty())
            return false;

        switch (type)
        {
            case BOT: return length <= EMBED_MAX_LENGTH_BOT;
            case CLIENT: return length <= EMBED_MAX_LENGTH_CLIENT;
            default: throw new IllegalArgumentException(String.format("Cannot check against AccountType '%s'!", type));
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MessageEmbed))
            return false;
        if (obj == this)
            return true;
        MessageEmbed other = (MessageEmbed) obj;
        return Objects.equals(url, other.url)
            && Objects.equals(title, other.title)
            && Objects.equals(description, other.description)
            && Objects.equals(type, other.type)
            && Objects.equals(thumbnail, other.thumbnail)
            && Objects.equals(siteProvider, other.siteProvider)
            && Objects.equals(author, other.author)
            && Objects.equals(videoInfo, other.videoInfo)
            && Objects.equals(footer, other.footer)
            && Objects.equals(image, other.image)
            && (color & 0xFFFFFF) == (other.color & 0xFFFFFF)
            && Objects.equals(timestamp, other.timestamp)
            && Helpers.deepEquals(fields, other.fields);
    }


    @Nonnull
    @Override
    public DataObject toData()
    {
        if (json != null)
            return json;
        synchronized (mutex)
        {
            if (json != null)
                return json;
            DataObject obj = DataObject.empty();
            if (url != null)
                obj.put("url", url);
            if (title != null)
                obj.put("title", title);
            if (description != null)
                obj.put("description", description);
            if (timestamp != null)
                obj.put("timestamp", timestamp.format(DateTimeFormatter.ISO_INSTANT));
            if (color != Role.DEFAULT_COLOR_RAW)
                obj.put("color", color & 0xFFFFFF);
            if (thumbnail != null)
                obj.put("thumbnail", DataObject.empty().put("url", thumbnail.getUrl()));
            if (siteProvider != null)
            {
                DataObject siteProviderObj = DataObject.empty();
                if (siteProvider.getName() != null)
                    siteProviderObj.put("name", siteProvider.getName());
                if (siteProvider.getUrl() != null)
                    siteProviderObj.put("url", siteProvider.getUrl());
                obj.put("provider", siteProviderObj);
            }
            if (author != null)
            {
                DataObject authorObj = DataObject.empty();
                if (author.getName() != null)
                    authorObj.put("name", author.getName());
                if (author.getUrl() != null)
                    authorObj.put("url", author.getUrl());
                if (author.getIconUrl() != null)
                    authorObj.put("icon_url", author.getIconUrl());
                obj.put("author", authorObj);
            }
            if (videoInfo != null)
                obj.put("video", DataObject.empty().put("url", videoInfo.getUrl()));
            if (footer != null)
            {
                DataObject footerObj = DataObject.empty();
                if (footer.getText() != null)
                    footerObj.put("text", footer.getText());
                if (footer.getIconUrl() != null)
                    footerObj.put("icon_url", footer.getIconUrl());
                obj.put("footer", footerObj);
            }
            if (image != null)
                obj.put("image", DataObject.empty().put("url", image.getUrl()));
            if (!fields.isEmpty())
            {
                DataArray fieldsArray = DataArray.empty();
                for (Field field : fields)
                {
                    fieldsArray
                        .add(DataObject.empty()
                            .put("name", field.getName())
                            .put("value", field.getValue())
                            .put("inline", field.isInline()));
                }
                obj.put("fields", fieldsArray);
            }
            return json = obj;
        }
    }


    public static class Thumbnail
    {
        protected final String url;
        protected final String proxyUrl;
        protected final int width;
        protected final int height;

        public Thumbnail(String url, String proxyUrl, int width, int height)
        {
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.width = width;
            this.height = height;
        }


        @Nullable
        public String getUrl()
        {
            return url;
        }


        @Nullable
        public String getProxyUrl()
        {
            return proxyUrl;
        }


        public int getWidth()
        {
            return width;
        }


        public int getHeight()
        {
            return height;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Thumbnail))
                return false;
            Thumbnail thumbnail = (Thumbnail) obj;
            return thumbnail == this || (Objects.equals(thumbnail.url, url)
                && Objects.equals(thumbnail.proxyUrl, proxyUrl)
                && thumbnail.width == width
                && thumbnail.height == height);
        }
    }


    public static class Provider
    {
        protected final String name;
        protected final String url;

        public Provider(String name, String url)
        {
            this.name = name;
            this.url = url;
        }


        @Nullable
        public String getName()
        {
            return name;
        }


        @Nullable
        public String getUrl()
        {
            return url;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Provider))
                return false;
            Provider provider = (Provider) obj;
            return provider == this || (Objects.equals(provider.name, name)
                && Objects.equals(provider.url, url));
        }
    }


    public static class VideoInfo
    {
        protected final String url;
        protected final int width;
        protected final int height;

        public VideoInfo(String url, int width, int height)
        {
            this.url = url;
            this.width = width;
            this.height = height;
        }


        @Nullable
        public String getUrl()
        {
            return url;
        }


        public int getWidth()
        {
            return width;
        }


        public int getHeight()
        {
            return height;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof VideoInfo))
                return false;
            VideoInfo video = (VideoInfo) obj;
            return video == this || (Objects.equals(video.url, url)
                && video.width == width
                && video.height == height);
        }
    }
    

    public static class ImageInfo
    {
        protected final String url;
        protected final String proxyUrl;
        protected final int width;
        protected final int height;

        public ImageInfo(String url, String proxyUrl, int width, int height)
        {
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.width = width;
            this.height = height;
        }


        @Nullable
        public String getUrl()
        {
            return url;
        }
        

        @Nullable
        public String getProxyUrl()
        {
            return proxyUrl;
        }


        public int getWidth()
        {
            return width;
        }


        public int getHeight()
        {
            return height;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ImageInfo))
                return false;
            ImageInfo image = (ImageInfo) obj;
            return image == this || (Objects.equals(image.url, url)
                && Objects.equals(image.proxyUrl, proxyUrl)
                && image.width == width
                && image.height == height);
        }
    }
    

    public static class AuthorInfo
    {
        protected final String name;
        protected final String url;
        protected final String iconUrl;
        protected final String proxyIconUrl;

        public AuthorInfo(String name, String url, String iconUrl, String proxyIconUrl)
        {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
            this.proxyIconUrl = proxyIconUrl;
        }


        @Nullable
        public String getName()
        {
            return name;
        }


        @Nullable
        public String getUrl()
        {
            return url;
        }
        

        @Nullable
        public String getIconUrl()
        {
            return iconUrl;
        }
        

        @Nullable
        public String getProxyIconUrl()
        {
            return proxyIconUrl;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof AuthorInfo))
                return false;
            AuthorInfo author = (AuthorInfo) obj;
            return author == this || (Objects.equals(author.name, name)
                && Objects.equals(author.url, url)
                && Objects.equals(author.iconUrl, iconUrl)
                && Objects.equals(author.proxyIconUrl, proxyIconUrl));
        }
    }
    

    public static class Footer
    {
        protected final String text;
        protected final String iconUrl;
        protected final String proxyIconUrl;

        public Footer(String text, String iconUrl, String proxyIconUrl)
        {
            this.text = text;
            this.iconUrl = iconUrl;
            this.proxyIconUrl = proxyIconUrl;
        }


        @Nullable
        public String getText()
        {
            return text;
        }
        

        @Nullable
        public String getIconUrl()
        {
            return iconUrl;
        }
        

        @Nullable
        public String getProxyIconUrl()
        {
            return proxyIconUrl;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Footer))
                return false;
            Footer footer = (Footer) obj;
            return footer == this || (Objects.equals(footer.text, text)
                && Objects.equals(footer.iconUrl, iconUrl)
                && Objects.equals(footer.proxyIconUrl, proxyIconUrl));
        }
    }
    

    public static class Field
    {
        protected final String name;
        protected final String value;
        protected final boolean inline;

        public Field(String name, String value, boolean inline, boolean checked)
        {
            if (checked)
            {
                if (name == null || value == null)
                    throw new IllegalArgumentException("Both Name and Value must be set!");
                else if (name.length() > TITLE_MAX_LENGTH)
                    throw new IllegalArgumentException("Name cannot be longer than " + TITLE_MAX_LENGTH + " characters.");
                else if (value.length() > VALUE_MAX_LENGTH)
                    throw new IllegalArgumentException("Value cannot be longer than " + VALUE_MAX_LENGTH + " characters.");
                name = name.trim();
                value = value.trim();
                if (name.isEmpty())
                    this.name = EmbedBuilder.ZERO_WIDTH_SPACE;
                else
                    this.name = name;
                if (value.isEmpty())
                    this.value = EmbedBuilder.ZERO_WIDTH_SPACE;
                else
                    this.value = value;
                this.inline = inline;
            }
            else
            {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }
        }
        
        public Field(String name, String value, boolean inline)
        {
            this(name, value, inline, true);
        }


        @Nullable
        public String getName()
        {
            return name;
        }


        @Nullable
        public String getValue()
        {
            return value;
        }
        

        public boolean isInline()
        {
            return inline;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Field))
                return false;
            final Field field = (Field) obj;
            return field == this || (field.inline == inline
                && Objects.equals(field.name, name)
                && Objects.equals(field.value, value));
        }
    }
}
