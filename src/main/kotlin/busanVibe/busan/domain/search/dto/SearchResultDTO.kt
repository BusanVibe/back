package busanVibe.busan.domain.search.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class SearchResultDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ListDto(
        val sort: String,
        val resultList: List<InfoDto>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class InfoDto(
        val typeKr: String,
        val typeEn: String,
        val id: Long? = null,
        val name: String,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val address: String,
        val congestionLevel: Int? = null,
        @get:JsonProperty("is_like")
        val isLike: Boolean,
        val startDate: String? = null,
        val endDate: String? = null,
        @get:JsonProperty("is_end")
        val isEnd: Boolean?,
        val likeCount: Int = 0
    )

}