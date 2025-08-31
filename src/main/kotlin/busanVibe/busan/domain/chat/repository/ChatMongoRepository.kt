package busanVibe.busan.domain.chat.repository

import busanVibe.busan.domain.chat.domain.ChatMessage
import busanVibe.busan.domain.chat.enums.MessageType
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ChatMongoRepository: MongoRepository<ChatMessage, String> {

    @Query("{ \"\$or\": [ { \"type\": { \"\$in\": ['BOT_REQUEST', 'BOT_RESPONSE'] }, \"userId\": ?0 }, { \"type\": { \"\$nin\": ['BOT_REQUEST', 'BOT_RESPONSE'] } } ] }")
    fun findAllWithBotFilter(userId: Long, pageable: Pageable): List<ChatMessage>

    @Query("{ \"\$and\": [ { \"_id\": { \"\$lt\": ?1 } }, { \"\$or\": [ { \"type\": { \"\$in\": ['BOT_REQUEST', 'BOT_RESPONSE'] }, \"userId\": ?0 }, { \"type\": { \"\$nin\": ['BOT_REQUEST', 'BOT_RESPONSE'] } } ] } ] }")
    fun findByIdLessThanWithBotFilter(userId: Long, cursorId: String, pageable: Pageable): List<ChatMessage>


}