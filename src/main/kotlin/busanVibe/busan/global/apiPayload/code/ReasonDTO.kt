package busanVibe.busan.global.apiPayload.code

import org.springframework.http.HttpStatus

class ReasonDTO(
    val httpStatus: HttpStatus?,
    val isSuccess: Boolean,
    val code: String,
    val message: String
) {

    fun getIsSuccess(): Boolean = isSuccess

}