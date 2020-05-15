

package net.dv8tion.jda.api.requests;

import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.IOFunction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.IOUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Response implements Closeable
{
    public static final int ERROR_CODE = -1;
    public static final String ERROR_MESSAGE = "ERROR";
    public static final IOFunction<BufferedReader, DataObject> JSON_SERIALIZE_OBJECT = DataObject::fromJson;
    public static final IOFunction<BufferedReader, DataArray> JSON_SERIALIZE_ARRAY = DataArray::fromJson;

    public final int code;
    public final String message;
    public final long retryAfter;
    private final InputStream body;
    private final okhttp3.Response rawResponse;
    private final Set<String> cfRays;
    private String fallbackString;
    private Object object;
    private boolean attemptedParsing = false;
    private Exception exception;

    public Response(@Nullable final okhttp3.Response response, @Nonnull final Exception exception, @Nonnull final Set<String> cfRays)
    {
        this(response, response != null ? response.code() : ERROR_CODE, ERROR_MESSAGE, -1, cfRays);
        this.exception = exception;
    }

    public Response(@Nullable final okhttp3.Response response, final int code, @Nonnull final String message, final long retryAfter, @Nonnull final Set<String> cfRays)
    {
        this.rawResponse = response;
        this.code = code;
        this.message = message;
        this.exception = null;
        this.retryAfter = retryAfter;
        this.cfRays = cfRays;

        if (response == null)
        {
            this.body = null;
        }
        else // weird compatibility issue, thinks some final isn't initialized if we return pre-maturely
        try
        {
            this.body = IOUtil.getBody(response);
        }
        catch (final Exception e)
        {
            throw new IllegalStateException("An error occurred while parsing the response for a RestAction", e);
        }
    }

    public Response(final long retryAfter, @Nonnull final Set<String> cfRays)
    {
        this(null, 429, "TOO MANY REQUESTS", retryAfter, cfRays);
    }

    public Response(@Nonnull final okhttp3.Response response, final long retryAfter, @Nonnull final Set<String> cfRays)
    {
        this(response, response.code(), response.message(), retryAfter, cfRays);
    }

    @Nonnull
    public DataArray getArray()
    {
        return get(DataArray.class, JSON_SERIALIZE_ARRAY);
    }

    @Nonnull
    public Optional<DataArray> optArray()
    {
        return parseBody(true, DataArray.class, JSON_SERIALIZE_ARRAY);
    }

    @Nonnull
    public DataObject getObject()
    {
        return get(DataObject.class, JSON_SERIALIZE_OBJECT);
    }

    @Nonnull
    public Optional<DataObject> optObject()
    {
        return parseBody(true, DataObject.class, JSON_SERIALIZE_OBJECT);
    }

    @Nonnull
    public String getString()
    {
        return parseBody(String.class, this::readString)
            .orElseGet(() -> fallbackString == null ? "N/A" : fallbackString);
    }

    @Nonnull
    public <T> T get(Class<T> clazz, IOFunction<BufferedReader, T> parser)
    {
        return parseBody(clazz, parser).orElseThrow(IllegalStateException::new);
    }

    @Nullable
    public okhttp3.Response getRawResponse()
    {
        return this.rawResponse;
    }

    @Nonnull
    public Set<String> getCFRays()
    {
        return cfRays;
    }

    @Nullable
    public Exception getException()
    {
        return exception;
    }

    public boolean isError()
    {
        return this.code == Response.ERROR_CODE;
    }

    public boolean isOk()
    {
        return this.code > 199 && this.code < 300;
    }

    public boolean isRateLimit()
    {
        return this.code == 429;
    }

    @Override
    public String toString()
    {
        return this.exception == null
                ? "HTTPResponse[" + this.code + (this.object == null ? "" : ", " + this.object.toString()) + ']'
                : "HTTPException[" + this.exception.getMessage() + ']';
    }

    @Override
    public void close()
    {
        if (rawResponse != null)
            rawResponse.close();
    }

    private String readString(BufferedReader reader)
    {
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private <T> Optional<T> parseBody(Class<T> clazz, IOFunction<BufferedReader, T> parser)
    {
        return parseBody(false, clazz, parser);
    }

    @SuppressWarnings("ConstantConditions")
    private <T> Optional<T> parseBody(boolean opt, Class<T> clazz, IOFunction<BufferedReader, T> parser)
    {
        if (attemptedParsing)
        {
            if (object != null && clazz.isAssignableFrom(object.getClass()))
                return Optional.of(clazz.cast(object));
            return Optional.empty();
        }

        attemptedParsing = true;
        if (body == null || rawResponse == null || rawResponse.body().contentLength() == 0)
            return Optional.empty();

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(body));
            reader.mark(1024);
            T t = parser.apply(reader);
            this.object = t;
            return Optional.ofNullable(t);
        }
        catch (final Exception e)
        {
            try
            {
                reader.reset();
                this.fallbackString = readString(reader);
                reader.close();
            }
            catch (NullPointerException | IOException ignored) {}
            if (opt && e instanceof ParsingException)
                return Optional.empty();
            else
                throw new IllegalStateException("An error occurred while parsing the response for a RestAction", e);
        }
    }
}
