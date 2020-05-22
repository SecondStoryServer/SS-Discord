package me.syari.ss.discord.api.exceptions

import me.syari.ss.discord.api.requests.ErrorResponse
import me.syari.ss.discord.api.requests.Response

class ErrorResponseException private constructor(
    response: Response?, code: Int, meaning: String
): RuntimeException("$code: $meaning") {
    init {
        if (response?.exception != null) {
            initCause(response.exception)
        }
    }

    companion object {
        fun create(
            errorResponse: ErrorResponse, response: Response
        ): ErrorResponseException {
            val dataObject = response.optObject()
            var meaning = errorResponse.meaning
            var code = errorResponse.code
            if (response.isError && response.exception != null) {
                code = response.code
                meaning = response.exception?.javaClass?.name ?: "null"
            } else if (dataObject != null) {
                val nullableCode = dataObject.getInt("code")
                val nullableMessage = dataObject.getString("message")
                if (nullableCode != null || nullableMessage != null) {
                    if (nullableCode != null) code = nullableCode
                    if (nullableMessage != null) meaning = nullableMessage
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