package busanVibe.busan.domain.user.data.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class TokenResponseDto(
    val accessToken: String?,
    val refreshToken: String?
) {

    companion object{
        fun of(accessToken: String?, refreshToken: String?): TokenResponseDto{
            return TokenResponseDto(accessToken, refreshToken)
        }
    }

}