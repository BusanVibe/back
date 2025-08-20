package busanVibe.busan.global.apiPayload.exception

import busanVibe.busan.global.apiPayload.code.ErrorReasonDTO
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice(annotations = [RestController::class, Controller::class])
class ExceptionAdvice : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler
    fun validation(e: ConstraintViolationException, request: WebRequest): ResponseEntity<Any>? {
        val errorMessage = e.constraintViolations
            .map { it.message }
            .firstOrNull()
            ?: throw RuntimeException("ConstraintViolationException 추출 도중 에러 발생")

        return handleExceptionInternalConstraint(e, ErrorStatus.valueOf(errorMessage), HttpHeaders.EMPTY, request)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = linkedMapOf<String, String>()

        ex.bindingResult.fieldErrors.forEach { fieldError ->
            val fieldName = fieldError.field
            val errorMessage = fieldError.defaultMessage ?: ""
            errors.merge(fieldName, errorMessage) { old, new -> "$old, $new" }
        }

        return handleExceptionInternalArgs(ex, HttpHeaders.EMPTY, ErrorStatus.BAD_REQUEST, request, errors)
    }

    // 잘못된 body 요청 처리
    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errorMessage = when {
            e.message?.contains("Enum") == true -> "요청 값이 올바르지 않습니다. Enum 타입을 확인해주세요."
            e.message?.contains("JSON parse error") == true -> "요청 Body의 JSON 형식이 올바르지 않습니다."
            e.message?.contains("Required request body is missing") == true -> "요청 Body가 비어있습니다."
            else -> "잘못된 요청 본문입니다. 요청 형식을 확인해주세요."
        }

        return handleExceptionInternalArgs(
            e,
            headers,
            ErrorStatus.BAD_REQUEST,
            request,
            mapOf("body" to errorMessage)
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        e: MethodArgumentTypeMismatchException,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val paramName = e.name
        val invalidValue = e.value
        val requiredType = e.requiredType

        val errorMessage = when {
            requiredType?.isEnum == true -> {
                val enumValues = requiredType.enumConstants.joinToString(", ")
                "잘못된 enum 값입니다. [$paramName=$invalidValue], 가능한 값: [$enumValues]"
            }
            requiredType == Long::class.java || requiredType == Int::class.java -> {
                "숫자 타입 파라미터가 잘못되었습니다. [$paramName=$invalidValue]"
            }
            requiredType == java.time.LocalDateTime::class.java -> {
                "날짜/시간 형식이 잘못되었습니다. [$paramName=$invalidValue], 예: 2024-01-01T12:00:00"
            }
            requiredType == java.time.LocalDate::class.java -> {
                "날짜 형식이 잘못되었습니다. [$paramName=$invalidValue], 예: 2024-01-01"
            }
            else -> {
                "요청 파라미터 타입이 잘못되었습니다. [$paramName=$invalidValue], 기대 타입: ${requiredType?.simpleName ?: "알 수 없음"}"
            }
        }

        val errorArgs = mapOf(paramName to errorMessage)

        return handleExceptionInternalArgs(
            e,
            HttpHeaders.EMPTY,
            ErrorStatus.BAD_REQUEST,
            request,
            errorArgs
        )
    }

    @ExceptionHandler
    fun exception(e: Exception, request: WebRequest): ResponseEntity<Any>? {
        log.error("Internal Exception", e)
        return handleExceptionInternalFalse(
            e,
            ErrorStatus.INTERNAL_SERVER_ERROR,
            HttpHeaders.EMPTY,
            ErrorStatus.INTERNAL_SERVER_ERROR.httpStatus,
            request,
            e.message ?: "알 수 없는 에러"
        )
    }

    @ExceptionHandler(GeneralException::class)
    fun onThrowException(ex: GeneralException, request: HttpServletRequest): ResponseEntity<Any>? {
        val reason = ex.getErrorReasonHttpStatus()
        return handleExceptionInternal(ex, reason, null, request)
    }

    private fun handleExceptionInternal(
        e: Exception,
        reason: ErrorReasonDTO,
        headers: HttpHeaders?,
        request: HttpServletRequest
    ): ResponseEntity<Any>? {
        val body = ApiResponse.onFailure(reason.code, reason.message, null)
        val webRequest = ServletWebRequest(request)
        return super.handleExceptionInternal(e, body, headers ?: HttpHeaders.EMPTY, reason.httpStatus, webRequest)
    }

    private fun handleExceptionInternalFalse(
        e: Exception,
        errorCommonStatus: ErrorStatus,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest,
        errorPoint: String
    ): ResponseEntity<Any>? {
        val body = ApiResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, errorPoint)
        return super.handleExceptionInternal(e, body, headers, status, request)
    }




    private fun handleExceptionInternalArgs(
        e: Exception,
        headers: HttpHeaders,
        errorCommonStatus: ErrorStatus,
        request: WebRequest,
        errorArgs: Map<String, String>
    ): ResponseEntity<Any>? {
        val body = ApiResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, errorArgs)
        return super.handleExceptionInternal(e, body, headers, errorCommonStatus.httpStatus, request)
    }

    private fun handleExceptionInternalConstraint(
        e: Exception,
        errorCommonStatus: ErrorStatus,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val body = ApiResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, null)
        return super.handleExceptionInternal(e, body, headers, errorCommonStatus.httpStatus, request)
    }
}