package busanVibe.busan.domain.chat.service

import busanVibe.busan.domain.chat.dto.websocket.ChatMessageReceiveDTO
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Component

@Component
class RedisPublisher(
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    fun publish(topic: ChannelTopic, message: ChatMessageReceiveDTO) {
        // 문자열이 아니라 객체를 그대로 넘긴다
        redisTemplate.convertAndSend(topic.topic, message)
    }
}