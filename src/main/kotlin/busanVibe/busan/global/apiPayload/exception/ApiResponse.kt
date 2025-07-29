package busanVibe.busan.global.apiPayload.exception

import busanVibe.busan.global.apiPayload.code.BaseCode
import busanVibe.busan.global.apiPayload.code.BaseErrorCode
import busanVibe.busan.global.apiPayload.code.status.SuccessStatus
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import lombok.AllArgsConstructor
import lombok.Getter

@Getter
@AllArgsConstructor
@JsonPropertyOrder("is_success", "code", "message", "result")
class ApiResponse<T>(
    @get:JsonProperty("is_success")
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: T?
) {


    companion object{

        fun <T> onSuccess(result: T): ApiResponse<T> {
            return ApiResponse(
                isSuccess = true,
                code = SuccessStatus._OK.code,
                message = SuccessStatus._OK.message,
                result = result
            )
        }

        fun <T> of(code: BaseCode, result: T): ApiResponse<T> {
            return ApiResponse(
                isSuccess = true,
                code = code.getReasonHttpStatus().code,
                message = code.getReasonHttpStatus().message,
                result = result
            )
        }

        fun <T> of(code: BaseErrorCode, result: T): ApiResponse<T> {
            return ApiResponse(
                isSuccess = true,
                code = code.getReasonHttpStatus().code,
                message = code.getReasonHttpStatus().message,
                result = result
            )
        }

        fun <T> onFailure(code: String, message: String, data: T?): ApiResponse<T> {
            return ApiResponse(
                isSuccess = false,
                code = code,
                message = message,
                result = data
            )
        }
    }


}