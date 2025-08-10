package busanVibe.busan.global.config.webSocket

import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import busanVibe.busan.global.config.security.JwtTokenProvider
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

class JwtHandshakeInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>
    ): Boolean {
        val servletRequest = (request as? ServletServerHttpRequest)?.servletRequest
        val authHeader = servletRequest?.getHeader("Authorization") ?: return false
        if (!authHeader.startsWith("Bearer ")) return false
        val token = authHeader.substring(7)

        if (!jwtTokenProvider.validateToken(token)) {
            return false
        }

        val userEmail = jwtTokenProvider.getIdFromToken(token)
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { ExceptionHandler(ErrorStatus.USER_NOT_FOUND) }

        // Spring Security 인증 객체 생성
        val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)

        // WebSocket session attributes에 Principal로 넣어줌
        attributes["principal"] = auth

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, exception: Exception?
    ) {
        // ...
    }
}
