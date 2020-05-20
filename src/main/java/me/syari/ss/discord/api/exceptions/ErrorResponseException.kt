package me.syari.ss.discord.api.exceptions

import me.syari.ss.discord.api.requests.ErrorResponse
import me.syari.ss.discord.api.requests.Response
import org.jetbrains.annotations.Contract

class ErrorResponseException private constructor(
    response: Response?, code: Int, meaning: String
): RuntimeException("$code: $meaning") {
    init {
        if (response?.exception != null) {
            initCause(response.exception)
        }
    }

    companion object {
        @JvmStatic
        @Contract("_, _ -> new")
        fun create(
            errorResponse: ErrorResponse, response: Response
        ): ErrorResponseException {
            val dataObject = response.optObject()
            var meaning = errorResponse.meaning
            var code = errorResponse.code
            if (response.isError && response.exception != null) {
                code = response.code
                meaning = response.exception!!.javaClass.name
            } else if (dataObject != null) {
                val isNullCode = dataObject.isNull("code")
                val isNullMessage = dataObject.isNull("message")
                if (!isNullCode || !isNullMessage) {
                    if (!isNullCode) code = dataObject.getInt("code")!!
                    if (!isNullMessage) meaning = dataObject.getString("message")!!
                } else {
                    code = response.code
                    meaning = dataObject.toString()
                }
            } else {
                code = response.code
                meaning = response.string
            }
            return ErrorResponseException(response, code, meaning)
        }
    }

}