

package me.syari.ss.discord.internal.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class BufferedRequestBody extends RequestBody
{
    private final Source source;
    private final MediaType type;
    private byte[] data;

    public BufferedRequestBody(Source source, MediaType type)
    {
        this.source = source;
        this.type = type;
    }

    @Nullable
    @Override
    public MediaType contentType()
    {
        return type;
    }

    @Override
    public void writeTo(@Nonnull BufferedSink sink) throws IOException
    {
        if (data != null)
        {
            sink.write(data);
            return;
        }

        try (BufferedSource s = Okio.buffer(source))
        {
            data = s.readByteArray();
            sink.write(data);
        }
    }
}
