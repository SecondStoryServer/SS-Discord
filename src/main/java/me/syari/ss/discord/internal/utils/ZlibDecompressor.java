package me.syari.ss.discord.internal.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class ZlibDecompressor {
    private static final Logger LOG = JDALogger.getLog(ZlibDecompressor.class);
    private static final int Z_SYNC_FLUSH = 0x0000FFFF;

    private final int maxBufferSize = 2048;
    private final Inflater inflater = new Inflater();
    private ByteBuffer flushBuffer = null;
    private SoftReference<ByteArrayOutputStream> decompressBuffer = null;

    public ZlibDecompressor() {
    }

    @Contract(" -> new")
    private @NotNull SoftReference<ByteArrayOutputStream> newDecompressBuffer() {
        return new SoftReference<>(new ByteArrayOutputStream(Math.min(1024, maxBufferSize)));
    }

    private ByteArrayOutputStream getDecompressBuffer() {
        if (decompressBuffer == null)
            decompressBuffer = newDecompressBuffer();
        ByteArrayOutputStream buffer = decompressBuffer.get();
        if (buffer == null) {
            decompressBuffer = new SoftReference<>(buffer = new ByteArrayOutputStream(Math.min(1024, maxBufferSize)));
        }
        return buffer;
    }

    private boolean isFlush(byte @NotNull [] data) {
        if (data.length < 4) {
            return false;
        }
        int suffix = IOUtil.getIntBigEndian(data, data.length - 4);
        return suffix == Z_SYNC_FLUSH;
    }

    private void buffer(byte[] data) {
        if (flushBuffer == null)
            flushBuffer = ByteBuffer.allocate(data.length * 2);

        if (flushBuffer.capacity() < data.length + flushBuffer.position()) {
            flushBuffer.flip();
            flushBuffer = IOUtil.reallocate(flushBuffer, (flushBuffer.capacity() + data.length) * 2);
        }

        flushBuffer.put(data);
    }

    @Contract(pure = true)
    private @NotNull Object lazy(byte[] data) {
        return JDALogger.getLazyString(() -> Arrays.toString(data));
    }

    public void reset() {
        inflater.reset();
    }

    public String decompress(byte[] data) throws DataFormatException {
        if (!isFlush(data)) {
            LOG.debug("Received incomplete data, writing to buffer. Length: {}", data.length);
            buffer(data);
            return null;
        } else if (flushBuffer != null) {
            LOG.debug("Received final part of incomplete data");
            buffer(data);
            byte[] arr = flushBuffer.array();
            data = new byte[flushBuffer.position()];
            System.arraycopy(arr, 0, data, 0, data.length);
            flushBuffer = null;
        }
        LOG.trace("Decompressing data {}", lazy(data));
        ByteArrayOutputStream buffer = getDecompressBuffer();
        try (InflaterOutputStream decompressor = new InflaterOutputStream(buffer, inflater)) {
            decompressor.write(data);
            return buffer.toString("UTF-8");
        } catch (IOException e) {
            throw (DataFormatException) new DataFormatException("Malformed").initCause(e);
        } finally {
            if (buffer.size() > maxBufferSize)
                decompressBuffer = newDecompressBuffer();
            else
                buffer.reset();
        }
    }
}
