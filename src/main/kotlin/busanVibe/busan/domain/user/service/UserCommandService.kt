package busanVibe.busan.domain.user.service

import busanVibe.busan.domain.user.converter.UserConverter
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.data.dto.login.KaKaoUserInfoResponseDTO
import busanVibe.busan.domain.user.data.dto.login.KakaoTokenResponseDTO
import busanVibe.busan.domain.user.data.dto.login.UserLoginRequestDTO
import busanVibe.busan.domain.user.data.dto.login.UserLoginResponseDTO
import busanVibe.busan.domain.user.enums.LoginType
import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.GeneralException
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import busanVibe.busan.global.config.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID
import kotlin.jvm.java

@Service
class UserCommandService(
    @Qualifier("kakaoTokenClient")
    private val kakaoTokenWebClient: WebClient,
    @Qualifier("kakaoUserInfoWebClient")
    private val kakaoUserInfoWebClient: WebClient,
    private val userRepository: UserRepository,
    private val userConverter: UserConverter,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${image.guest}")
    private val guestImage: String

) {

    @Value("\${spring.kakao.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.kakao.redirect-uri}")
    private lateinit var redirectUri: String

    private val log = LoggerFactory.getLogger(this::class.java)

    fun guestLogin(): UserLoginResponseDTO.LoginDto {

        val email = UUID.randomUUID().toString().substring(0,7) + "@busanvibe.com"
        val nickname = "guest" + UUID.randomUUID().toString().substring(0, 4)
        val profileImageUrl: String? = guestImage

        return isNewUser(email, nickname, profileImageUrl, LoginType.GUEST)
    }

    /**
     * 로컬 로그인
     */
    fun localLogin(requestDTO: UserLoginRequestDTO.LocalLoginDto): UserLoginResponseDTO.LoginDto{

        // request body 로부터 정보 가져옴
        val email = requestDTO.email
        val password = requestDTO.password

        // 유저 조회
        val user = userRepository.findByEmail(email)
            .orElseThrow { ExceptionHandler(ErrorStatus.USER_NOT_FOUND) }

        // 비밀번호 검증 - 틀리면 예외 발생
        if(!passwordEncoder.matches(password, user.passwordHash)){
            throw GeneralException(ErrorStatus.LOGIN_INVALID_PASSWORD)
        }

        // 토큰 생성 및 반환
        log.info("[LOCAL LOGIN] email : {}", email)
        val token = jwtTokenProvider.createToken(user)
        return userConverter.toLoginDto(user, false, token)
    }

    /**
     * 로컬 회원가입
     */
    fun localSignUp(requestDTO: UserLoginRequestDTO.LocalSignUpDto) {

        // 요청받은 값들
        val email = requestDTO.email
        val password = requestDTO.password

        // 이메일 중복 검사
        if(userRepository.existsByEmail(email)) // 이메일 이미 존재하면 예외 발생
            throw GeneralException(ErrorStatus.SIGNUP_EMAIL_EXISTS)

        // 이메일 앞부분 추출하여 nickname 생성
        lateinit var nickname: String
        try{
            nickname = email.substring(0, email.indexOf("@"))
        }catch (e: Exception) {
            throw GeneralException(ErrorStatus.INVALID_EMAIL_STYLE)
        }
        val profileImageUrl: String? = null

        // password encode
        val encodedPassword = passwordEncoder.encode(password)

        // 유저 저장
        userRepository.save(User(
            email = email,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            loginType = LoginType.LOCAL,
            passwordHash = encodedPassword
        ))


    }

    /**
     * 카카오 로그인
     */
    fun loginOrRegisterByKakao(code: String): UserLoginResponseDTO.LoginDto {
        val token: KakaoTokenResponseDTO = getKakaoToken(code)
        val userInfo = getUserInfo(token.accessToken)

        val email = userInfo.kakaoAccount?.email ?: throw GeneralException(ErrorStatus.USER_NOT_FOUND)
        val nickname = userInfo.kakaoAccount.profile?.nickName ?: "\uCE74\uCE74\uC624 \uC0AC\uC6A9\uC790"
        val profileImageUrl = userInfo.kakaoAccount.profile?.profileImageUrl ?: ""

        return isNewUser(email, nickname, profileImageUrl, LoginType.KAKAO)
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
        email: String,
        nickname: String,
        profileImageUrl: String?,
        loginType: LoginType
    ): UserLoginResponseDTO.LoginDto {
        val user = userRepository.findByEmail(email)

        return user.map {
            log.info("[{} LOGIN] 기존 유저 로그인: {}", loginType.name, email)
            val token = jwtTokenProvider.createToken(it)
            userConverter.toLoginDto(it, false, token)
        }.orElseGet {
            log.info("[{} LOGIN] 신규 유저 회원가입: {}", loginType.name, email)

            val newUser = User(
                email = email,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                loginType = loginType
            )

            userRepository.save(newUser)
            val token = jwtTokenProvider.createToken(newUser)
            userConverter.toLoginDto(newUser, true, token)
        }
    }
}
