package busanVibe.busan.domain.user.service.login

import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.springframework.security.core.context.SecurityContextHolder

class AuthService(
) {


    fun getCurrentUser(): User {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ExceptionHandler(ErrorStatus.AUTHENTICATION_FAILED)

        val principal = authentication.principal
            ?: throw ExceptionHandler(ErrorStatus.AUTHENTICATION_FAILED)

        if (principal !is User) {
            throw ExceptionHandler(ErrorStatus.AUTHENTICATION_FAILED)
        }

        return principal
    }


    fun getCurrentUserId(): Long?{
        return getCurrentUser().id
    }

    fun getCurrentUserEmail(): String?{
        return getCurrentUser().email
    }


}