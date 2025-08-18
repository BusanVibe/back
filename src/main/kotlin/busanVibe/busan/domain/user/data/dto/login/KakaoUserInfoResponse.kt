package busanVibe.busan.domain.user.data.dto.login

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KaKaoUserInfoResponseDTO(

    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount? = null
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class KakaoAccount(

        @JsonProperty("email")
        val email: String? = null,

        @JsonProperty("profile")
        val profile: Profile? = null
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Profile(

            @JsonProperty("nickname")
            val nickName: String? = null,

            @JsonProperty("profile_image_url")
            val profileImageUrl: String? = null
        )
    }
}