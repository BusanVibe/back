package busanVibe.busan.domain.user.data.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class UserLikesDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ListDto(
        val resultList: List<LikesInfoDto>
    )

    data class LikesInfoDto(
        val typeKr: String,
        val typeEn: String,
        val id: Long? = null,
        val name: String,
        val address: String,
        @get:JsonProperty("is_liked")
        val isLiked: Boolean,
//        val startDate: String? = null,
//        val endDate: String? = null,
        @get:JsonProperty("is_end")
        val isEnd: Boolean?
    )

}