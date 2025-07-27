package busanVibe.busan.domain.festival.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class FestivalDetailsDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class DetailDto(
        val id: Long,
        val img: String,
        val name: String,
        val likeCount: Integer,
        val isLike: Boolean,
        val startDate: String,
        val endDate: String,
        val region: String,
        val phone: String,
        val fee: Integer,
        val introduce: String
    )

}