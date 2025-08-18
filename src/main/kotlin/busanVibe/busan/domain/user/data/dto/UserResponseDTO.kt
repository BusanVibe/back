package busanVibe.busan.domain.user.data.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class UserResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MyPageDto(
        val nickname: String,
        val email: String,
        val userImageUrl: String? = null
    )

}