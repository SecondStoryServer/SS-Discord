

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


    private ErrorResponseException(ErrorResponse errorResponse, Response response, int code, String meaning)
    {
        super(code + ": " + meaning);

        this.response = response;
        if (response != null && response.getException() != null)
            initCause(response.getException());
        this.errorResponse = errorResponse;
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


}
