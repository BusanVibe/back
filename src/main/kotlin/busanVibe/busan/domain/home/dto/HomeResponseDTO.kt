package busanVibe.busan.domain.home.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class HomeResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class HomeResultDto(
        val mostCrowded: List<MostCongestion>,
        val recommendPlace: List<RecommendPlace>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MostCongestion(
        val placeId: Long?,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val type: String,
        val image: String?,
        val congestionLevel: Int,
        val address: String
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class RecommendPlace(
        val placeId: Long?,
        val name: String,
        val congestionLevel: Int,
        val type: String,
        val image: String?,
        val latitude: Double,
        val longitude: Double,
        val address: String,
        @get:JsonProperty("is_liked")
        val isLiked: Boolean
    )

}