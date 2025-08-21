package busanVibe.busan.domain.chat.repository

import busanVibe.busan.domain.chat.domain.ChatMessage
import busanVibe.busan.domain.chat.enums.MessageType
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatMongoRepository: MongoRepository<ChatMessage, String> {
    fun findAllByOrderByTimeDesc(pageable: Pageable): List<ChatMessage>
    fun findByIdLessThanOrderByTimeDesc(id: String, pageable: Pageable): List<ChatMessage>
    fun findAllByTypeOrderByTimeDesc(type: MessageType, pageable: Pageable): List<ChatMessage>
    fun findByIdLessThanAndTypeOrderByTimeDesc(id: String, type: MessageType, pageable: Pageable): List<ChatMessage>

}