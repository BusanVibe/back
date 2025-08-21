package busanVibe.busan.domain.chat.dto.openai

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class WebSearchRequest(
    val model: String,
    val messages: List<Message>,
//    val tools: List<Tool>? = null,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Message(
    val role: String,
    val content: String
)

data class Tool(
    val type: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ApproximateLocation(
    val country: String,
    val city: String,
    val region: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class WebSearchResponse(
    val id: String,
    val choices: List<Choice>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Choice(
    val index: Int,
    val message: Message
)
