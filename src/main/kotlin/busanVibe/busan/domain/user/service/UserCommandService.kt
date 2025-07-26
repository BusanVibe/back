package busanVibe.busan.domain.user.service

import busanVibe.busan.domain.user.converter.UserConverter
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.data.dto.KaKaoUserInfoResponseDTO
import busanVibe.busan.domain.user.data.dto.KakaoTokenResponseDTO
import busanVibe.busan.domain.user.data.dto.TokenResponseDto
import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.global.config.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import kotlin.jvm.java

@Service
class UserCommandService(
    @Qualifier("kakaoTokenClient")
    private val kakaoTokenWebClient: WebClient,
    @Qualifier("kakaoUserInfoWebClient")
    private val kakaoUserInfoWebClient: WebClient,
    private val userRepository: UserRepository,
    private val userConverter: UserConverter,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Value("\${spring.kakao.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.kakao.redirect-uri}")
    private lateinit var redirectUri: String

    private val log = LoggerFactory.getLogger(this::class.java)

    fun loginOrRegisterByKakao(code: String): UserResponseDTO.LoginDto {
        val token: KakaoTokenResponseDTO = getKakaoToken(code)
        val userInfo = getUserInfo(token.accessToken)

        val email = userInfo.kakaoAccount?.email
        val nickname = userInfo.kakaoAccount?.profile?.nickName ?: "\uCE74\uCE74\uC624 \uC0AC\uC6A9\uC790"
        val profileImageUrl = userInfo.kakaoAccount?.profile?.profileImageUrl ?: ""

        val tokenResponseDTO = TokenResponseDto.of(token.accessToken, token.refreshToken)

        return isNewUser(email, nickname, profileImageUrl, tokenResponseDTO)
    }

    private fun getKakaoToken(code: String): KakaoTokenResponseDTO {
        val response = kakaoTokenWebClient
            .post()
            .uri("/oauth/token")
            .body(
                BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("client_id", clientId)
                    .with("redirect_uri", redirectUri)
                    .with("code", code)
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                Mono.error(RuntimeException("Invalid Parameter"))
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Internal Server Error"))
            }
            .bodyToMono(KakaoTokenResponseDTO::class.java)
            .block()

        log.info("[Kakao Service] Access Token ------> {}", response?.accessToken)
        log.info("[Kakao Service] Refresh Token ------> {}", response?.refreshToken)

        return response ?: throw RuntimeException("Kakao token fetch failed")
    }

    private fun getUserInfo(accessToken: String?): KaKaoUserInfoResponseDTO {
        return kakaoUserInfoWebClient
            .get()
            .uri("/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .bodyToMono(KaKaoUserInfoResponseDTO::class.java)
            .block()
            ?: throw RuntimeException("Failed to fetch user info from Kakao")
    }

    private fun isNewUser(
        email: String?,
        nickname: String,
        profileImageUrl: String?,
        tokenResponseDto: TokenResponseDto
    ): UserResponseDTO.LoginDto {
        val user = userRepository.findByEmail(email)

        return user.map {
            log.info("기존 유저 로그인: {}", email)
            val token = jwtTokenProvider.createToken(it)
            userConverter.toLoginDto(it, false, token)
        }.orElseGet {
            log.info("신규 유저 회원가입: {}", email)

            val newUser = User(
                email = email,
                nickname = nickname,
                profileImageUrl = profileImageUrl
            )

            userRepository.save(newUser)
            val token = jwtTokenProvider.createToken(newUser)
            userConverter.toLoginDto(newUser, true, token)
        }
    }
}
