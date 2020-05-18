package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.data.DataObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ErrorResponseException extends RuntimeException {
    private ErrorResponseException(Response response, int code, String meaning) {
        super(code + ": " + meaning);
        if (response != null && response.getException() != null) {
            initCause(response.getException());
        }
    }

    @Contract("_, _ -> new")
    public static @NotNull ErrorResponseException create(@NotNull ErrorResponse errorResponse, @NotNull Response response) {
        Optional<DataObject> optObject = response.optObject();
        String meaning = errorResponse.getMeaning();
        int code = errorResponse.getCode();
        if (response.isError() && response.getException() != null) {
            code = response.code;
            meaning = response.getException().getClass().getName();
        } else if (optObject.isPresent()) {
            DataObject object = optObject.get();
            boolean isNullCode = object.isNull("code");
            boolean isNullMessage = object.isNull("message");
            if (!isNullCode || !isNullMessage) {
                if (!isNullCode) {
                    code = object.getInt("code");
                }
                if (!isNullMessage) {
                    meaning = object.getString("message");
                }
            } else {
                code = response.code;
                meaning = object.toString();
            }
        } else {
            code = response.code;
            meaning = response.getString();
        }

        return new ErrorResponseException(response, code, meaning);
    }
}
