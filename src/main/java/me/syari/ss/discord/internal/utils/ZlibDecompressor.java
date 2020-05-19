package me.syari.ss.discord.internal.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class ZlibDecompressor {
    private static final int Z_SYNC_FLUSH = 0x0000FFFF;

    private final int maxBufferSize = 2048;
    private final Inflater inflater = new Inflater();
    private ByteBuffer flushBuffer = null;
    private SoftReference<ByteArrayOutputStream> decompressBuffer = null;

    @Contract(" -> new")
    private @NotNull SoftReference<ByteArrayOutputStream> newDecompressBuffer() {
        return new SoftReference<>(new ByteArrayOutputStream(Math.min(1024, maxBufferSize)));
    }

    private ByteArrayOutputStream getDecompressBuffer() {
        if (decompressBuffer == null) decompressBuffer = newDecompressBuffer();
        ByteArrayOutputStream buffer = decompressBuffer.get();
        if (buffer == null)
            decompressBuffer = new SoftReference<>(buffer = new ByteArrayOutputStream(Math.min(1024, maxBufferSize)));
        return buffer;
    }

    private int getIntBigEndian(@NotNull byte[] array, int offset) {
        return array[offset + 3] & 0xFF | (array[offset + 2] & 0xFF) << 8 | (array[offset + 1] & 0xFF) << 16 | (array[offset] & 0xFF) << 24;
    }

    private boolean isFlush(@NotNull byte[] data) {
        if (data.length < 4) return false;
        int suffix = getIntBigEndian(data, data.length - 4);
        return suffix == Z_SYNC_FLUSH;
    }

    private @NotNull ByteBuffer reallocate(ByteBuffer original, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(original);
        return buffer;
    }

    private void buffer(byte[] data) {
        if (flushBuffer == null) flushBuffer = ByteBuffer.allocate(data.length * 2);
        if (flushBuffer.capacity() < data.length + flushBuffer.position()) {
            flushBuffer.flip();
            flushBuffer = reallocate(flushBuffer, (flushBuffer.capacity() + data.length) * 2);
        }
        flushBuffer.put(data);
    }

    public void reset() {
        inflater.reset();
    }

    public String decompress(byte[] data) throws DataFormatException {
        if (!isFlush(data)) {
            buffer(data);
            return null;
        } else if (flushBuffer != null) {
            buffer(data);
            byte[] arr = flushBuffer.array();
            data = new byte[flushBuffer.position()];
            System.arraycopy(arr, 0, data, 0, data.length);
            flushBuffer = null;
        }
        ByteArrayOutputStream buffer = getDecompressBuffer();
        try (InflaterOutputStream decompressor = new InflaterOutputStream(buffer, inflater)) {
            decompressor.write(data);
            return buffer.toString("UTF-8");
        } catch (IOException e) {
            throw (DataFormatException) new DataFormatException("Malformed").initCause(e);
        } finally {
            if (buffer.size() > maxBufferSize) {
                decompressBuffer = newDecompressBuffer();
            } else {
                buffer.reset();
            }
        }
    }
}
