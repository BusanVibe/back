package busanVibe.busan.global.apiPayload.code

import lombok.Builder
import org.springframework.http.HttpStatus

@Builder
open class ErrorReasonDTO(
    val httpStatus: HttpStatus,
    val isSuccess: Boolean = false,
    val code: String,
    val message: String
){
    companion object{
        fun of(status: HttpStatus, code: String, message: String): ErrorReasonDTO =
            ErrorReasonDTO(status, false, code, message)
    }
}