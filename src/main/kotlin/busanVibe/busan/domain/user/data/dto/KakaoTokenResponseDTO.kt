package busanVibe.busan.domain.user.data.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoTokenResponseDTO(

    @JsonProperty("token_type")
    val tokenType: String? = null,

    @JsonProperty("access_token")
    val accessToken: String? = null,

    @JsonProperty("id_token")
    val idToken: String? = null,

    @JsonProperty("expires_in")
    val expiresIn: Int? = null,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Int? = null,

    @JsonProperty("scope")
    val scope: String? = null
)
