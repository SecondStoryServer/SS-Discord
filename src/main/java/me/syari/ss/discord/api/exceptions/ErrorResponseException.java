package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.data.DataObject;

import java.util.Optional;


public class ErrorResponseException extends RuntimeException {
    private final Response response;


    private ErrorResponseException(Response response, int code, String meaning) {
        super(code + ": " + meaning);

        this.response = response;
        if (response != null && response.getException() != null)
            initCause(response.getException());
    }


    public static ErrorResponseException create(ErrorResponse errorResponse, Response response) {
        Optional<DataObject> optObj = response.optObject();
        String meaning = errorResponse.getMeaning();
        int code = errorResponse.getCode();
        if (response.isError() && response.getException() != null) {
            // this generally means that an exception occurred trying to
            //make an http request. e.g.:
            //SocketTimeoutException/ UnknownHostException
            code = response.code;
            meaning = response.getException().getClass().getName();
        } else if (optObj.isPresent()) {
            DataObject obj = optObj.get();
            if (!obj.isNull("code") || !obj.isNull("message")) {
                if (!obj.isNull("code"))
                    code = obj.getInt("code");
                if (!obj.isNull("message"))
                    meaning = obj.getString("message");
            } else {
                // This means that neither code or message is provided
                //In that case we simply put the raw response in place!
                code = response.code;
                meaning = obj.toString();
            }
        } else {
            // error response body is not JSON
            code = response.code;
            meaning = response.getString();
        }

        return new ErrorResponseException(response, code, meaning);
    }


}
