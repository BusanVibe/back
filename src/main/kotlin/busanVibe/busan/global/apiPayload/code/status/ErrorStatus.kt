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

    // 로그인 관련
    SIGNUP_EMAIL_EXISTS(HttpStatus.BAD_REQUEST, "LOGIN4001", "이미 존재하는 이메일입니다."),
    INVALID_EMAIL_STYLE(HttpStatus.BAD_REQUEST, "LOGIN4002", "이메일 형식이 올바르지 않습니다."),
    LOGIN_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "LOGIN4003", "비밀번호가 올바르지 않습니다."),

    // 명소 관련 에러
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE4004", "명소를 찾을 수 없습니다."),

    // 축제 관련 에러
    FESTIVAL_NOT_FOUND(HttpStatus.NOT_FOUND, "FESTIVAL4004", "축제를 찾을 수 없습니다."),

    // 검색 관련 에러
    SEARCH_INVALID_CONDITION(HttpStatus.BAD_REQUEST, "SEARCH4002", "잘못된 검색 조건입니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "인증에 실패했습니다."),

    // 채팅 관련 에러
    CHAT_INVALID_LENGTH(HttpStatus.BAD_REQUEST, "CHAT4001", "글자 수는 200자로 제한됩니다."),
    INVALID_PAGE_SIZE_MINUS(HttpStatus.BAD_REQUEST, "CHAT4002", "페이지 크기는 음수일 수 없습니다."),
    INVALID_PAGE_SIZE_BIG(HttpStatus.BAD_REQUEST, "CHAT4003", "페이지 크기는 30을 넘을 수 없습니다."),
    CHAT_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT4004", "채팅에 해당하는 유저를 찾을 수 없습니다."),

    // 지도 관련 에러
    MAP_LATITUDE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "MAP4001", "위도는 -90~90 사이여야 합니다."),
    MAP_LONGITUDE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "MAP4002", "경도는 -180~180 사이여야 합니다."),
    MAP_INVALID_COORDINATE_ORDER(HttpStatus.BAD_REQUEST, "MAP4003", "좌측상단 좌표가 우측하단 좌표보다 위/왼쪽에 있어야 합니다."),

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