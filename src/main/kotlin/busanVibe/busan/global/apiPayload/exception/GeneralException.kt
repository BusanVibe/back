package busanVibe.busan.global.apiPayload.exception

import busanVibe.busan.global.apiPayload.code.BaseErrorCode
import busanVibe.busan.global.apiPayload.code.ErrorReasonDTO
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter

@Getter
@AllArgsConstructor
@Builder
open class GeneralException(
    private val code: BaseErrorCode
) : RuntimeException() {

    fun getErrorReason(): ErrorReasonDTO {
        return code.getReason()
    }

    fun getErrorReasonHttpStatus(): ErrorReasonDTO {
        return code.getReasonHttpStatus()
    }
}