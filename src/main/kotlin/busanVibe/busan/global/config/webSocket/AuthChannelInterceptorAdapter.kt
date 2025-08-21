package busanVibe.busan.global.config.webSocket

import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.global.config.security.JwtTokenProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthChannelInterceptorAdapter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : ChannelInterceptor {

    private val log: Logger = LoggerFactory.getLogger(AuthChannelInterceptorAdapter::class.java)

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)
        if (StompCommand.CONNECT == accessor.command) {
            val authHeader = accessor.getFirstNativeHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw IllegalArgumentException("No Authorization header")
            }

            val token = authHeader.substring(7)
            if (!jwtTokenProvider.validateToken(token)) {
                throw IllegalArgumentException("Invalid token")
            }

            val userId = jwtTokenProvider.getIdFromToken(token)
            val user = userRepository.findById(userId.toLong()).orElseThrow {
                IllegalArgumentException("User not found")
            }

            log.info("[Web Socket] 웹소켓 연결 유저: id=${user.id}, email=${user.email}")

            val auth = jwtTokenProvider.getAuthentication(token)
            accessor.user = auth
            SecurityContextHolder.getContext().authentication = auth
        }

        return message
    }



}