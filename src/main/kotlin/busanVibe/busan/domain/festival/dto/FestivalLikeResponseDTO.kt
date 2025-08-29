package busanVibe.busan.domain.festival.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FestivalLikeResponseDTO(
    val success: Boolean,
    val message: String,
) {

}