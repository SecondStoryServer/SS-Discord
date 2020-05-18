package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.exceptions.ParsingException;
import me.syari.ss.discord.api.utils.IOFunction;
import me.syari.ss.discord.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class Response implements Closeable {
    public static final int ERROR_CODE = -1;
    public static final IOFunction<BufferedReader, DataObject> JSON_SERIALIZE_OBJECT = DataObject::fromJson;

    public final int code;
    public final long retryAfter;
    private final InputStream body;
    private final okhttp3.Response rawResponse;
    private String fallbackString;
    private Object object;
    private boolean attemptedParsing = false;
    private Exception exception;

    public Response(@Nullable final okhttp3.Response response, @NotNull final Exception exception) {
        this(response, response != null ? response.code() : ERROR_CODE, -1);
        this.exception = exception;
    }

    @SuppressWarnings("ConstantConditions")
    public @Nullable InputStream getBody(okhttp3.@NotNull Response response) throws IOException {
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

    public Response(@Nullable final okhttp3.Response response, final int code, final long retryAfter) {
        this.rawResponse = response;
        this.code = code;
        this.exception = null;
        this.retryAfter = retryAfter;

        if (response == null) {
            this.body = null;
        } else {
            try {
                this.body = getBody(response);
            } catch (final Exception ex) {
                throw new IllegalStateException("An error occurred while parsing the response for a RestAction", ex);
            }
        }
    }

    public Response(final long retryAfter) {
        this(null, 429, retryAfter);
    }

    public Response(@NotNull final okhttp3.Response response, final long retryAfter) {
        this(response, response.code(), retryAfter);
    }

    @NotNull
    public DataObject getObject() {
        return parseBody(DataObject.class, Response.JSON_SERIALIZE_OBJECT).orElseThrow(IllegalStateException::new);
    }

    @NotNull
    public Optional<DataObject> optObject() {
        return parseBody(true, DataObject.class, JSON_SERIALIZE_OBJECT);
    }

    @NotNull
    public String getString() {
        return parseBody(String.class, this::readString).orElseGet(() -> fallbackString == null ? "N/A" : fallbackString);
    }

    @Nullable
    public Exception getException() {
        return exception;
    }

    public boolean isError() {
        return this.code == Response.ERROR_CODE;
    }

    public boolean isOk() {
        return this.code > 199 && this.code < 300;
    }

    public boolean isRateLimit() {
        return this.code == 429;
    }

    @Override
    public String toString() {
        return this.exception == null
                ? "HTTPResponse[" + this.code + (this.object == null ? "" : ", " + this.object.toString()) + ']'
                : "HTTPException[" + this.exception.getMessage() + ']';
    }

    @Override
    public void close() {
        if (rawResponse != null) {
            rawResponse.close();
        }
    }

    private String readString(@NotNull BufferedReader reader) {
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private <T> Optional<T> parseBody(Class<T> clazz, IOFunction<BufferedReader, T> parser) {
        return parseBody(false, clazz, parser);
    }

    @SuppressWarnings("ConstantConditions")
    private <T> Optional<T> parseBody(boolean opt, Class<T> clazz, IOFunction<BufferedReader, T> parser) {
        if (attemptedParsing) {
            if (object != null && clazz.isAssignableFrom(object.getClass()))
                return Optional.of(clazz.cast(object));
            return Optional.empty();
        }

        attemptedParsing = true;
        if (body == null || rawResponse == null || rawResponse.body().contentLength() == 0) {
            return Optional.empty();
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(body));
            reader.mark(1024);
            T t = parser.apply(reader);
            this.object = t;
            return Optional.ofNullable(t);
        } catch (final Exception e) {
            try {
                reader.reset();
                this.fallbackString = readString(reader);
                reader.close();
            } catch (NullPointerException | IOException ex) {
                ex.printStackTrace();
            }
            if (opt && e instanceof ParsingException) {
                return Optional.empty();
            } else {
                throw new IllegalStateException("An error occurred while parsing the response for a RestAction", e);
            }
        }
    }
}
