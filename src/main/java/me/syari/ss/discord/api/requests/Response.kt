package me.syari.ss.discord.api.requests

import me.syari.ss.discord.api.exceptions.ParsingException
import me.syari.ss.discord.api.data.DataObject
import okhttp3.Response
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.Closeable
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import java.util.zip.ZipException

class Response(private val rawResponse: Response?, val code: Int, val retryAfter: Long): Closeable {
    private var body: InputStream?
    private var fallbackString: String? = null
    private var anyData: Any? = null
    private var attemptedParsing = false
    var exception: Exception? = null
        private set

    constructor(response: Response?, exception: Exception): this(
        response, response?.code ?: ERROR_CODE, -1
    ) {
        this.exception = exception
    }

    @Throws(IOException::class)
    private fun getBody(response: Response): InputStream? {
        val encoding = response.header("content-encoding", "")
        val data: InputStream = BufferedInputStream(response.body!!.byteStream())
        data.mark(256)
        try {
            if (encoding.equals("gzip", ignoreCase = true)) {
                return GZIPInputStream(data)
            } else if (encoding.equals("deflate", ignoreCase = true)) {
                return InflaterInputStream(data, Inflater(true))
            }
        } catch (ex: ZipException) {
            data.reset()
            return null
        } catch (ex: EOFException) {
            data.reset()
            return null
        }
        return data
    }

    constructor(retryAfter: Long): this(null, 429, retryAfter)
    constructor(response: Response, retryAfter: Long): this(response, response.code, retryAfter)

    val dataObject: DataObject
        get() = parseBody(DataObject::class.java, JSON_SERIALIZE_OBJECT) ?: throw IllegalStateException()

    fun optObject(): DataObject? {
        return parseBody(true, DataObject::class.java, JSON_SERIALIZE_OBJECT)
    }

    val string: String
        get() = parseBody(String::class.java) { reader ->
            readString(reader)
        } ?: fallbackString ?: "N/A"

    val isError: Boolean
        get() = code == ERROR_CODE

    val isOk: Boolean
        get() = code in 200..299

    val isRateLimit: Boolean
        get() = code == 429

    override fun toString(): String {
        return if (exception == null) "HTTPResponse[" + code + (if (this.anyData == null) "" else ", " + this.anyData.toString()) + ']' else "HTTPException[" + exception!!.message + ']'
    }

    override fun close() {
        rawResponse?.close()
    }

    private fun readString(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    private fun <T> parseBody(clazz: Class<T>, parser: (BufferedReader) -> T): T? {
        return parseBody(false, clazz, parser)
    }

    private fun <T> parseBody(
        opt: Boolean, clazz: Class<T>, parser: (BufferedReader) -> T
    ): T? {
        if (attemptedParsing) {
            return anyData?.let {
                if (clazz.isAssignableFrom(it.javaClass)) {
                    clazz.cast(it)
                } else {
                    null
                }
            }
        }
        attemptedParsing = true
        if (body == null || rawResponse == null || rawResponse.body!!.contentLength() == 0L) {
            return null
        }
        val reader = BufferedReader(InputStreamReader(body))
        return try {
            reader.mark(1024)
            parser.invoke(reader).apply {
                anyData = this
            }
        } catch (ex1: Exception) {
            try {
                reader.reset()
                fallbackString = readString(reader)
                reader.close()
            } catch (ex2: NullPointerException) {
                ex2.printStackTrace()
            } catch (ex2: IOException) {
                ex2.printStackTrace()
            }
            if (opt && ex1 is ParsingException) {
                null
            } else {
                throw IllegalStateException("An error occurred while parsing the response for a RestAction", ex1)
            }
        }
    }

    companion object {
        const val ERROR_CODE = -1
        val JSON_SERIALIZE_OBJECT = { stream: BufferedReader -> DataObject.fromJson(stream) }
    }

    init {
        body = if (rawResponse == null) {
            null
        } else {
            try {
                getBody(rawResponse)
            } catch (ex: Exception) {
                throw IllegalStateException("An error occurred while parsing the response for a RestAction", ex)
            }
        }
    }
}