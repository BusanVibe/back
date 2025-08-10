package busanVibe.busan.domain.chat.dto.websocket

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class WebSocketErrorDTO(
    val errorCode: String,
    val errorMessage: String
)