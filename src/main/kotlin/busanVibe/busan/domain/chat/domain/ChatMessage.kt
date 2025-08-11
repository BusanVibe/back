package busanVibe.busan.domain.chat.domain

import busanVibe.busan.domain.chat.enums.MessageType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
data class ChatMessage(
    @Id
    val id: String? = null,

    val type: MessageType,

    val userId: Long?,

    val message: String,

    val time: LocalDateTime,

)