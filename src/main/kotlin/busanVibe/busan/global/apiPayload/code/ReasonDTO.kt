package busanVibe.busan.global.apiPayload.code

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.http.HttpStatus

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class ReasonDTO(
    val httpStatus: HttpStatus?,
    @get:JsonProperty("is_success")
    val isSuccess: Boolean,
    val code: String,
    val message: String
) {

    fun getIsSuccess(): Boolean = isSuccess

}