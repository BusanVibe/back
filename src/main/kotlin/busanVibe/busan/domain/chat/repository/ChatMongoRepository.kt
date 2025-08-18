package busanVibe.busan.domain.chat.repository

import busanVibe.busan.domain.chat.domain.ChatMessage
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatMongoRepository: MongoRepository<ChatMessage, String> {
    fun findAllByOrderByTimeDesc(pageable: Pageable): List<ChatMessage>
    fun findByIdLessThanOrderByTimeDesc(id: String, pageable: Pageable): List<ChatMessage>
}