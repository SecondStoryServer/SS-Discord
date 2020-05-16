

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

public class IOUtil
{
    private static final Logger log = JDALogger.getLog(IOUtil.class);

    public static String getHost(String uri)
    {
        return URI.create(uri).getHost();
    }


    public static byte[] readFully(File file) throws IOException
    {
        Checks.notNull(file, "File");
        Checks.check(file.exists(), "Provided file does not exist!");

        try (InputStream is = new FileInputStream(file))
        {
            // Get the size of the file
            long length = file.length();

            // You cannot create an array using a long type.
            // It needs to be an int type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE)
            {
                throw new IOException("Cannot read the file into memory completely due to it being too large!");
                // File is too large
            }

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
            {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length)
            {
                throw new IOException("Could not completely read file " + file.getName());
            }

            // Close the input stream and return bytes
            is.close();
            return bytes;
        }
    }


    public static byte[] readFully(InputStream stream) throws IOException
    {
        Checks.notNull(stream, "InputStream");

        byte[] buffer = new byte[1024];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
        {
            int readAmount = 0;
            while ((readAmount = stream.read(buffer)) != -1)
            {
                bos.write(buffer, 0, readAmount);
            }
            return bos.toByteArray();
        }
    }


    public static RequestBody createRequestBody(final MediaType contentType, final InputStream stream)
    {
        return new BufferedRequestBody(Okio.source(stream), contentType);
    }

    public static short getShortBigEndian(byte[] arr, int offset)
    {
        return (short) ((arr[offset    ] & 0xff) << 8
                       | arr[offset + 1] & 0xff);
    }

    public static short getShortLittleEndian(byte[] arr, int offset)
    {
        // Same as big endian but reversed order of bytes (java uses big endian)
        return (short) ((arr[offset    ] & 0xff)
                      | (arr[offset + 1] & 0xff) << 8);
    }

    public static int getIntBigEndian(byte[] arr, int offset)
    {
        return arr[offset + 3] & 0xFF
            | (arr[offset + 2] & 0xFF) << 8
            | (arr[offset + 1] & 0xFF) << 16
            | (arr[offset    ] & 0xFF) << 24;
    }

    public static void setIntBigEndian(byte[] arr, int offset, int it)
    {
        arr[offset    ] = (byte) ((it >>> 24) & 0xFF);
        arr[offset + 1] = (byte) ((it >>> 16) & 0xFF);
        arr[offset + 2] = (byte) ((it >>> 8)  & 0xFF);
        arr[offset + 3] = (byte) ( it         & 0xFF);
    }

    public static ByteBuffer reallocate(ByteBuffer original, int length)
    {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(original);
        return buffer;
    }


    @SuppressWarnings("ConstantConditions") // methods here don't return null despite the annotations on them, read the docs
    public static InputStream getBody(okhttp3.Response response) throws IOException
    {
        String encoding = response.header("content-encoding", "");
        InputStream data = new BufferedInputStream(response.body().byteStream());
        data.mark(256);
        try
        {
            if (encoding.equalsIgnoreCase("gzip"))
                return new GZIPInputStream(data);
            else if (encoding.equalsIgnoreCase("deflate"))
                return new InflaterInputStream(data, new Inflater(true));
        }
        catch (ZipException | EOFException ex)
        {
            data.reset(); // reset to get full content
            log.error("Failed to read gzip content for response. Headers: {}\nContent: '{}'",
                response.headers(), JDALogger.getLazyString(() -> new String(readFully(data))), ex);
            return null;
        }
        return data;
    }
}
