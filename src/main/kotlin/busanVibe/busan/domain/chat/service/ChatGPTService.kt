package busanVibe.busan.domain.chat.service

import busanVibe.busan.domain.chat.dto.openai.ChatRequest
import busanVibe.busan.domain.chat.dto.openai.ChatResponse
import busanVibe.busan.global.config.openai.ChatGPTConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ChatGPTService(
    private val chatGPTConfig: ChatGPTConfig,

    @Value("\${openai.model}")
    private val model: String,

    @Value("\${openai.secret-key}")
    private val url: String

) {

    fun prompt(prompt: String): String {

        // 토큰 정보가 포함된 header 가져오기
        val headers = chatGPTConfig.httpHeaders()

        // create request
        val chatRequest = ChatRequest(model, prompt)

        // 통신을 위한 RestTemplate 구성
        val requestEntity = HttpEntity(chatRequest, headers)

        val restTemplate = RestTemplate()
        val response: ChatResponse? = restTemplate.postForObject(url, requestEntity, ChatResponse::class.java)

        if (response?.choices.isNullOrEmpty()) {
            throw RuntimeException("No choices returned from ChatGPT.")
        }

        return response!!.choices[0].message.content
    }
}
