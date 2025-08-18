package busanVibe.busan.domain.user.util

import busanVibe.busan.domain.user.data.dto.login.UserLoginResponseDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URLEncoder

@Component
class LoginRedirectUtil(
    @Value("\${spring.kakao.deep-link}")
    private val deepLink: String
){

    fun getRedirectHeader(userResponse: UserLoginResponseDTO.LoginDto): HttpHeaders{
        val accessTokenEncoded = URLEncoder.encode(userResponse.tokenResponseDTO.accessToken, "UTF-8")
        val refreshTokenEncoded = URLEncoder.encode(userResponse.tokenResponseDTO.refreshToken, "UTF-8")

        val redirectUrl = "$deepLink?accessToken=$accessTokenEncoded&refreshToken=$refreshTokenEncoded"

        val headers = HttpHeaders()
        headers.location = URI.create(redirectUrl)
        return headers

    }

}