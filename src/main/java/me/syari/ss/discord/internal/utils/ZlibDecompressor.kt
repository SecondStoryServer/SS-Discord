package me.syari.ss.discord.internal.utils

import okhttp3.internal.and
import org.jetbrains.annotations.Contract
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ref.SoftReference
import java.nio.ByteBuffer
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream

class ZlibDecompressor {
    private val maxBufferSize = 2048
    private val inflater = Inflater()
    private var flushBuffer: ByteBuffer? = null
    private var decompressBuffer: SoftReference<ByteArrayOutputStream>? = null

    @Contract(" -> new")
    private fun newDecompressBuffer(): SoftReference<ByteArrayOutputStream> {
        return SoftReference(ByteArrayOutputStream(Math.min(1024, maxBufferSize)))
    }

    private fun getDecompressBuffer(): ByteArrayOutputStream? {
        if (decompressBuffer == null) decompressBuffer = newDecompressBuffer()
        var buffer = decompressBuffer!!.get()
        if (buffer == null) decompressBuffer = SoftReference(ByteArrayOutputStream(
            Math.min(
                1024, maxBufferSize
            )
        ).also { buffer = it })
        return buffer
    }

    private fun getIntBigEndian(array: ByteArray, offset: Int): Int {
        return array[offset + 3] and 0xFF or (array[offset + 2] and 0xFF shl 8) or (array[offset + 1] and 0xFF shl 16) or (array[offset] and 0xFF shl 24)
    }

    private fun isFlush(data: ByteArray): Boolean {
        if (data.size < 4) return false
        val suffix = getIntBigEndian(data, data.size - 4)
        return suffix == Z_SYNC_FLUSH
    }

    private fun reallocate(original: ByteBuffer?, length: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(length)
        buffer.put(original)
        return buffer
    }

    private fun buffer(data: ByteArray) {
        if (flushBuffer == null) flushBuffer = ByteBuffer.allocate(data.size * 2)
        if (flushBuffer!!.capacity() < data.size + flushBuffer!!.position()) {
            flushBuffer!!.flip()
            flushBuffer = reallocate(flushBuffer, (flushBuffer!!.capacity() + data.size) * 2)
        }
        flushBuffer!!.put(data)
    }

    fun reset() {
        inflater.reset()
    }

    @Throws(DataFormatException::class)
    fun decompress(data: ByteArray): String? {
        var data = data
        if (!isFlush(data)) {
            buffer(data)
            return null
        } else if (flushBuffer != null) {
            buffer(data)
            val arr = flushBuffer!!.array()
            data = ByteArray(flushBuffer!!.position())
            System.arraycopy(arr, 0, data, 0, data.size)
            flushBuffer = null
        }
        val buffer = getDecompressBuffer()
        try {
            InflaterOutputStream(buffer, inflater).use { decompressor ->
                decompressor.write(data)
                return buffer!!.toString("UTF-8")
            }
        } catch (e: IOException) {
            throw (DataFormatException("Malformed").initCause(e) as DataFormatException)
        } finally {
            if (buffer!!.size() > maxBufferSize) {
                decompressBuffer = newDecompressBuffer()
            } else {
                buffer.reset()
            }
        }
    }

    companion object {
        private const val Z_SYNC_FLUSH = 0x0000FFFF
    }
}