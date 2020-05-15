

package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;


public class ErrorResponseException extends RuntimeException
{
    private final ErrorResponse errorResponse;
    private final Response response;
    private final String meaning;
    private final int code;


    private ErrorResponseException(ErrorResponse errorResponse, Response response, int code, String meaning)
    {
        super(code + ": " + meaning);

        this.response = response;
        if (response != null && response.getException() != null)
            initCause(response.getException());
        this.errorResponse = errorResponse;
        this.code = code;
        this.meaning = meaning;
    }


    public boolean isServerError()
    {
        return errorResponse == ErrorResponse.SERVER_ERROR;
    }


    public String getMeaning()
    {
        return meaning;
    }


    public int getErrorCode()
    {
        return code;
    }


    public ErrorResponse getErrorResponse()
    {
        return errorResponse;
    }


    public Response getResponse()
    {
        return response;
    }

    public static ErrorResponseException create(ErrorResponse errorResponse, Response response)
    {
        Optional<DataObject> optObj = response.optObject();
        String meaning = errorResponse.getMeaning();
        int code = errorResponse.getCode();
        if (response.isError() && response.getException() != null)
        {
            // this generally means that an exception occurred trying to
            //make an http request. e.g.:
            //SocketTimeoutException/ UnknownHostException
            code = response.code;
            meaning = response.getException().getClass().getName();
        }
        else if (optObj.isPresent())
        {
            DataObject obj = optObj.get();
            if (!obj.isNull("code") || !obj.isNull("message"))
            {
                if (!obj.isNull("code"))
                    code = obj.getInt("code");
                if (!obj.isNull("message"))
                    meaning = obj.getString("message");
            }
            else
            {
                // This means that neither code or message is provided
                //In that case we simply put the raw response in place!
                code = response.code;
                meaning = obj.toString();
            }
        }
        else
        {
            // error response body is not JSON
            code = response.code;
            meaning = response.getString();
        }

        return new ErrorResponseException(errorResponse, response, code, meaning);
    }


    @Nonnull
    public static Consumer<Throwable> ignore(@Nonnull Collection<ErrorResponse> set)
    {
        return ignore(RestAction.getDefaultFailure(), set);
    }


    @Nonnull
    public static Consumer<Throwable> ignore(@Nonnull ErrorResponse ignored, @Nonnull ErrorResponse... errorResponses)
    {
        return ignore(RestAction.getDefaultFailure(), ignored, errorResponses);
    }


    @Nonnull
    public static Consumer<Throwable> ignore(@Nonnull Consumer<? super Throwable> orElse, @Nonnull ErrorResponse ignored, @Nonnull ErrorResponse... errorResponses)
    {
        return ignore(orElse, EnumSet.of(ignored, errorResponses));
    }


    @Nonnull
    public static Consumer<Throwable> ignore(@Nonnull Consumer<? super Throwable> orElse, @Nonnull Collection<ErrorResponse> set)
    {
        Checks.notNull(orElse, "Callback");
        Checks.notEmpty(set, "Ignored collection");
        // Make an enum set copy (for performance, memory efficiency, and thread-safety)
        final EnumSet<ErrorResponse> ignored = EnumSet.copyOf(set);
        return (throwable) ->
        {
            if (throwable instanceof ErrorResponseException)
            {
                ErrorResponseException ex = (ErrorResponseException) throwable;
                if (ignored.contains(ex.getErrorResponse()))
                    return;
            }

            try
            {
                orElse.accept(throwable);
            }
            catch (Exception ex)
            {
                JDALogger.getLog(ErrorResponseException.class).error("Uncaught exception in ignore callback", throwable);
            }
        };
    }
}
