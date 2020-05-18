package me.syari.ss.discord.internal.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class IOUtil {
    public static String getHost(String uri) {
        return URI.create(uri).getHost();
    }
    public static int getIntBigEndian(byte @NotNull [] array, int offset) {
        return array[offset + 3] & 0xFF | (array[offset + 2] & 0xFF) << 8 | (array[offset + 1] & 0xFF) << 16 | (array[offset] & 0xFF) << 24;
    }

    public static @NotNull ByteBuffer reallocate(ByteBuffer original, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(original);
        return buffer;
    }

    @SuppressWarnings("ConstantConditions")
    public static @Nullable InputStream getBody(okhttp3.@NotNull Response response) throws IOException {
        String encoding = response.header("content-encoding", "");
        InputStream data = new BufferedInputStream(response.body().byteStream());
        data.mark(256);
        try {
            if (encoding.equalsIgnoreCase("gzip")) {
                return new GZIPInputStream(data);
            } else if (encoding.equalsIgnoreCase("deflate")) {
                return new InflaterInputStream(data, new Inflater(true));
            }
        } catch (ZipException | EOFException ex) {
            data.reset();
            return null;
        }
        return data;
    }
}
