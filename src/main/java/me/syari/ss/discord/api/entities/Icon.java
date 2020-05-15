

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.IOUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Icon
{
    protected final String encoding;

    protected Icon(@Nonnull IconType type, @Nonnull String base64Encoding)
    {
        //Note: the usage of `image/jpeg` does not mean png/gif are not supported!
        this.encoding = type.getHeader() + base64Encoding;
    }


    @Nonnull
    public String getEncoding()
    {
        return encoding;
    }


    @Nonnull
    public static Icon from(@Nonnull File file) throws IOException
    {
        Checks.notNull(file, "Provided File");
        Checks.check(file.exists(), "Provided file does not exist!");
        int index = file.getName().lastIndexOf('.');
        if (index < 0)
            return from(file, IconType.JPEG);
        String ext = file.getName().substring(index + 1);
        IconType type = IconType.fromExtension(ext);
        return from(file, type);
    }


    @Nonnull
    public static Icon from(@Nonnull InputStream stream) throws IOException
    {
        return from(stream, IconType.JPEG);
    }


    @Nonnull
    public static Icon from(@Nonnull byte[] data)
    {
        return from(data, IconType.JPEG);
    }


    @Nonnull
    public static Icon from(@Nonnull File file, @Nonnull IconType type) throws IOException
    {
        Checks.notNull(file, "Provided File");
        Checks.notNull(type, "IconType");
        Checks.check(file.exists(), "Provided file does not exist!");

        return from(IOUtil.readFully(file), type);
    }


    @Nonnull
    public static Icon from(@Nonnull InputStream stream, @Nonnull IconType type) throws IOException
    {
        Checks.notNull(stream, "InputStream");
        Checks.notNull(type, "IconType");

        return from(IOUtil.readFully(stream), type);
    }


    @Nonnull
    public static Icon from(@Nonnull byte[] data, @Nonnull IconType type)
    {
        Checks.notNull(data, "Provided byte[]");
        Checks.notNull(type, "IconType");

        return new Icon(type, new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8));
    }


    public enum IconType
    {

        JPEG("image/jpeg"),

        PNG("image/png"),

        WEBP("image/webp"),

        GIF("image/gif"),


        UNKNOWN("image/jpeg");

        private final String mime;
        private final String header;

        IconType(@Nonnull String mime)
        {
            this.mime = mime;
            this.header = "data:" + mime + ";base64,";
        }


        @Nonnull
        public String getMIME()
        {
            return mime;
        }


        @Nonnull
        public String getHeader()
        {
            return header;
        }


        @Nonnull
        public static IconType fromMIME(@Nonnull String mime)
        {
            Checks.notNull(mime, "MIME Type");
            for (IconType type : values())
            {
                if (type.mime.equalsIgnoreCase(mime))
                    return type;
            }
            return UNKNOWN;
        }


        @Nonnull
        public static IconType fromExtension(@Nonnull String extension)
        {
            Checks.notNull(extension, "Extension Type");
            switch (extension.toLowerCase())
            {
                case "jpe":
                case "jif":
                case "jfif":
                case "jfi":
                case "jpg":
                case "jpeg":
                    return JPEG;
                case "png":
                    return PNG;
                case "webp":
                    return WEBP;
                case "gif":
                    return GIF;
            }
            return UNKNOWN;
        }
    }
}
