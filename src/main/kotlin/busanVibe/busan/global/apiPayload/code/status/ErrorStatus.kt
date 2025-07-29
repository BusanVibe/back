package busanVibe.busan.global.apiPayload.code.status

import busanVibe.busan.global.apiPayload.code.BaseErrorCode
import busanVibe.busan.global.apiPayload.code.ErrorReasonDTO
import lombok.AllArgsConstructor
import org.springframework.http.HttpStatus

@AllArgsConstructor
enum class ErrorStatus(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
): BaseErrorCode {

    // 가장 일반적인 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "유저를 찾을 수 없습니다."),

    // 명소 관련 에러
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE4004", "명소를 찾을 수 없습니다."),

    // 축제 관련 에러
    FESTIVAL_NOT_FOUND(HttpStatus.NOT_FOUND, "FESTIVAL4004", "축제를 찾을 수 없습니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "인증에 실패했습니다.");

    ;

    override fun getReason(): ErrorReasonDTO {
        return ErrorReasonDTO(
            httpStatus = INTERNAL_SERVER_ERROR.httpStatus,
            isSuccess = false,
            code = code,
            message = message
        )
    }

    override fun getReasonHttpStatus(): ErrorReasonDTO {
        return ErrorReasonDTO(
            httpStatus = httpStatus,
            isSuccess = false,
            code = code,
            message = message
        )
    }

}