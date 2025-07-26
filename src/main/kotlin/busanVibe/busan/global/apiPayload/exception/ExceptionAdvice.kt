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
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice(annotations = [RestController::class])
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