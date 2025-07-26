package busanVibe.busan.global.apiPayload.code.status

import busanVibe.busan.global.apiPayload.code.BaseCode
import busanVibe.busan.global.apiPayload.code.ReasonDTO
import org.springframework.http.HttpStatus

enum class SuccessStatus(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
): BaseCode {

    _OK(HttpStatus.OK, "COMMON200", "성공입니다.");
    ;

    override fun getReason(): ReasonDTO {
        return ReasonDTO(null, true, code, message)
    }

    override fun getReasonHttpStatus(): ReasonDTO {
        return ReasonDTO(httpStatus, true, code, message)
    }

}