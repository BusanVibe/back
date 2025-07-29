package busanVibe.busan.global.apiPayload.code

import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.http.HttpStatus

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class ErrorResponseDTO private constructor(
    httpStatus: HttpStatus,
    code: String,
    message: String
) : ErrorReasonDTO(httpStatus, false, code, message) {

    companion object {
        fun of(errorStatus: ErrorStatus): ErrorResponseDTO {
            return ErrorResponseDTO(
                httpStatus = errorStatus.httpStatus,
                code = errorStatus.code,
                message = errorStatus.message
            )
        }

        fun of(errorStatus: ErrorStatus, message: String): ErrorResponseDTO {
            return ErrorResponseDTO(
                httpStatus = errorStatus.httpStatus,
                code = errorStatus.code,
                message = "${errorStatus.message} - $message"
            )
        }

        fun of(errorStatus: ErrorStatus, e: Exception): ErrorResponseDTO {
            return ErrorResponseDTO(
                httpStatus = errorStatus.httpStatus,
                code = errorStatus.code,
                message = errorStatus.message
            )
        }
    }


}