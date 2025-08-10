package busanVibe.busan.domain.chat.dto.websocket

import busanVibe.busan.domain.chat.enums.MessageType
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatMessageReceiveDTO(
    var userId: Long? = null,
    var name: String,
    var imageUrl: String? = null,
    var message: String,
    var time: LocalDateTime,
    var type: MessageType? = null,
) {
}