package busanVibe.busan.domain.chat.dto.websocket

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

class ChatMessageResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ListDto(
        val chatList: List<ChatInfoDto>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ChatInfoDto(
        val userName: String,
        val userImage: String?,
        val content: String,
        val dateTime: LocalDateTime?,
        @get:JsonProperty("is_my")
        val isMy: Boolean
    )

}