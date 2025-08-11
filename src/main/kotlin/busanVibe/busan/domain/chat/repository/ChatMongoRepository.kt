package busanVibe.busan.domain.chat.repository

import busanVibe.busan.domain.chat.domain.ChatMessage
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatMongoRepository: MongoRepository<ChatMessage, String> {
    fun findAllByOrderByTime(): List<ChatMessage>
}