package busanVibe.busan.domain.chat.dto.openai

data class ChatMessageDTO(
    val role: String,
    val content: String
)

data class ChatRequestDto(
    val model: String,
    val messages: List<ChatMessageDTO>,
    val n: Int = 1
) {
    constructor(model: String, prompt: String): this(
        model = model,
        messages = listOf(ChatMessageDTO("user", prompt)),
        n = 1
    )
}

data class ChatResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val message: ChatMessageDTO
    )
}
