package busanVibe.busan.domain.festival.dto

import com.fasterxml.jackson.annotation.JsonProperty

class FestivalListResponseDTO {

    data class ListDto(
        @JsonProperty("festival_list")
        val festivalList: List<FestivalInfoDto>
    )

    data class FestivalInfoDto(
        @JsonProperty("festival_id")
        val festivalId: Long,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("img")
        val img: String,
        @JsonProperty("start_date")
        val startDate: String,
        @JsonProperty("end_date")
        val endDate: String,
        @JsonProperty("region")
        val region: String,
        @JsonProperty("is_like")
        val isLike: Boolean
    )

}