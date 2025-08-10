package busanVibe.busan.global.config.openai

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@Configuration
class ChatGPTConfig(

    @Value("\${openai.secret-key}")
    private val secretKey: String,

    @Value("\${openai.model}")
    private val model: String

) {

    @Bean
    fun resTemplates(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun httpHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer $secretKey")
        headers.setContentType(MediaType.APPLICATION_JSON)

        return headers
    }

    fun getSecretKey(): String {
        return secretKey
    }

    fun getModel():String {
        return model
    }



}