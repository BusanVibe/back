package busanVibe.busan.domain.chat.dto.openai

class ChatRequest(
    val model: String,
    val messages: List<ChatMessageDTO>,
    val n: Int,
    val max_tokens: Int
) {
    constructor(model: String, prompt: String) : this(
        model = model,
        messages = listOf(ChatMessageDTO("user", prompt)),
        n = 1,
        max_tokens = 100
    )
}
