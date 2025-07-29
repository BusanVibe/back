package busanVibe.busan.global.apiPayload.code

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import lombok.Builder
import org.springframework.http.HttpStatus

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
open class ErrorReasonDTO(
    val httpStatus: HttpStatus,
    @get:JsonProperty("is_success")
    val isSuccess: Boolean = false,
    val code: String,
    val message: String
){
    companion object{
        fun of(status: HttpStatus, code: String, message: String): ErrorReasonDTO =
            ErrorReasonDTO(status, false, code, message)
    }
}