package me.syari.ss.discord.internal.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Okio;
import org.slf4j.Logger;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class IOUtil {
    private static final Logger log = JDALogger.getLog(IOUtil.class);

    public static String getHost(String uri) {
        return URI.create(uri).getHost();
    }


    public static byte[] readFully(InputStream stream) throws IOException {
        Checks.notNull(stream, "InputStream");

        byte[] buffer = new byte[1024];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int readAmount;
            while ((readAmount = stream.read(buffer)) != -1) {
                bos.write(buffer, 0, readAmount);
            }
            return bos.toByteArray();
        }
    }


    public static RequestBody createRequestBody(final MediaType contentType, final InputStream stream) {
        return new BufferedRequestBody(Okio.source(stream), contentType);
    }

    public static int getIntBigEndian(byte[] arr, int offset) {
        return arr[offset + 3] & 0xFF
                | (arr[offset + 2] & 0xFF) << 8
                | (arr[offset + 1] & 0xFF) << 16
                | (arr[offset] & 0xFF) << 24;
    }

    public static ByteBuffer reallocate(ByteBuffer original, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(original);
        return buffer;
    }


    @SuppressWarnings("ConstantConditions")
    // methods here don't return null despite the annotations on them, read the docs
    public static InputStream getBody(okhttp3.Response response) throws IOException {
        String encoding = response.header("content-encoding", "");
        InputStream data = new BufferedInputStream(response.body().byteStream());
        data.mark(256);
        try {
            if (encoding.equalsIgnoreCase("gzip"))
                return new GZIPInputStream(data);
            else if (encoding.equalsIgnoreCase("deflate"))
                return new InflaterInputStream(data, new Inflater(true));
        } catch (ZipException | EOFException ex) {
            data.reset(); // reset to get full content
            log.error("Failed to read gzip content for response. Headers: {}\nContent: '{}'",
                    response.headers(), JDALogger.getLazyString(() -> new String(readFully(data))), ex);
            return null;
        }
        return data;
    }
}
