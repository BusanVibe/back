package busanVibe.busan.domain.festival.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class FestivalListResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ListDto(
        val festivalList: List<FestivalInfoDto>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class FestivalInfoDto(
        val festivalId: Long,
        val name: String,
        val img: String?,
        val startDate: String,
        val endDate: String,
        val address: String,
        @get:JsonProperty("is_like")
        val isLike: Boolean,
        val likeCount: Int,
    )

}