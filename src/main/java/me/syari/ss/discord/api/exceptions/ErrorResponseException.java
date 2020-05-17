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

        if (response != null && response.getException() != null)
            initCause(response.getException());
    }

    @Contract("_, _ -> new")
    public static @NotNull ErrorResponseException create(@NotNull ErrorResponse errorResponse, @NotNull Response response) {
        Optional<DataObject> optObj = response.optObject();
        String meaning = errorResponse.getMeaning();
        int code = errorResponse.getCode();
        if (response.isError() && response.getException() != null) {
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
                code = response.code;
                meaning = obj.toString();
            }
        } else {
            code = response.code;
            meaning = response.getString();
        }

        return new ErrorResponseException(response, code, meaning);
    }


}
