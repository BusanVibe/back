package busanVibe.busan.domain.chat.service

import busanVibe.busan.domain.chat.dto.websocket.ChatMessageReceiveDTO
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class RedisSubscriber(
    private val messagingTemplate: SimpMessagingTemplate,
    private val serializer: GenericJackson2JsonRedisSerializer,
): MessageListener {

    private val log = LoggerFactory.getLogger(RedisSubscriber::class.java)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val chatMessage = serializer.deserialize(message.body, ChatMessageReceiveDTO::class.java) as? ChatMessageReceiveDTO
        if (chatMessage != null) {
            log.info("🔔 수신된 메시지: {}", chatMessage)
            messagingTemplate.convertAndSend("/sub/chatroom", chatMessage)
        } else {
            log.warn("❌ 수신된 메시지를 ChatMessageDTO로 변환할 수 없습니다.")
        }

    }

}