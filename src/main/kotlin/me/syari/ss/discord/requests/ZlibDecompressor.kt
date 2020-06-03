package me.syari.ss.discord.requests

import okhttp3.internal.and
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ref.SoftReference
import java.nio.ByteBuffer
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream

internal object ZlibDecompressor {
    private const val maxBufferSize = 2048
    private const val Z_SYNC_FLUSH = 0x0000FFFF
    private val inflater = Inflater()
    private var flushBuffer: ByteBuffer? = null
    private var decompressBuffer: SoftReference<ByteArrayOutputStream>? = null

    private fun newDecompressBufferReferent(): ByteArrayOutputStream {
        return ByteArrayOutputStream(1024.coerceAtMost(maxBufferSize))
    }

    private fun newDecompressBuffer(): SoftReference<ByteArrayOutputStream> {
        return SoftReference(newDecompressBufferReferent())
    }

    private fun getDecompressBuffer(): ByteArrayOutputStream {
        val notNullDecompressBuffer = decompressBuffer ?: {
            newDecompressBuffer().apply {
                decompressBuffer = this
            }
        }.invoke()
        return notNullDecompressBuffer.get() ?: {
            newDecompressBufferReferent().apply {
                decompressBuffer = SoftReference(this)
            }
        }.invoke()
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
        val notNullFlushBuffer = flushBuffer ?: {
            ByteBuffer.allocate(data.size * 2).apply {
                flushBuffer = this
            }
        }.invoke()
        if (notNullFlushBuffer.capacity() < data.size + notNullFlushBuffer.position()) {
            notNullFlushBuffer.flip()
            flushBuffer = reallocate(
                notNullFlushBuffer, (notNullFlushBuffer.capacity() + data.size) * 2
            )
        }
        notNullFlushBuffer.put(data)
    }

    fun reset() {
        inflater.reset()
    }

    @Throws(DataFormatException::class)
    fun decompress(data: ByteArray): String? {
        var dataAsMutable = data
        if (!isFlush(dataAsMutable)) {
            buffer(dataAsMutable)
            return null
        } else {
            flushBuffer?.let { flushBuffer ->
                buffer(dataAsMutable)
                val arr = flushBuffer.array()
                dataAsMutable = ByteArray(flushBuffer.position())
                System.arraycopy(arr, 0, dataAsMutable, 0, dataAsMutable.size)
                ZlibDecompressor.flushBuffer = null
            }
        }
        val buffer = getDecompressBuffer()
        try {
            InflaterOutputStream(buffer, inflater).use { decompressor ->
                decompressor.write(dataAsMutable)
                return buffer.toString("UTF-8")
            }
        } catch (ex: IOException) {
            throw DataFormatException("Malformed").initCause(ex) as DataFormatException
        } finally {
            if (maxBufferSize < buffer.size()) {
                decompressBuffer = newDecompressBuffer()
            } else {
                buffer.reset()
            }
        }
    }
}