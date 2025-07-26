package busanVibe.busan.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun kakaoTokenClient(): WebClient{

        return WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .build()
    }

    @Bean(name = ["kakaoUserInfoWebClient"])
    fun kakaoUserInfoWebClient(): WebClient{
        return WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();
    }
}