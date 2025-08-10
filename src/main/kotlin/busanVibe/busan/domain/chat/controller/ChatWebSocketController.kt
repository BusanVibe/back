package busanVibe.busan.domain.chat.controller

import busanVibe.busan.domain.chat.dto.websocket.ChatMessageSendDTO
import busanVibe.busan.domain.chat.service.ChatMongoService
import busanVibe.busan.domain.user.data.User
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class ChatWebSocketController(
    private val chatMongoService: ChatMongoService,
) {

    /**
     * websocket '/pub/chat/message' 로 들어오는 메시징 처리
     */
//    @MessageMapping("/chat/message")
//    fun handleMessage(@Payload chatMessage: ChatMessageSendDTO, authentication: Authentication) {
//        val user = authentication.principal as User
//        chatMongoService.saveAndPublish(chatMessage, user)
//    }

}