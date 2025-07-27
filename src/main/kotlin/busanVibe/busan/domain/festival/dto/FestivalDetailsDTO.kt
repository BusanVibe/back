package busanVibe.busan.domain.festival.dto

import com.fasterxml.jackson.annotation.JsonProperty

class FestivalDetailsDTO {

    data class DetailDto(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("img")
        val img: String,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("like_count")
        val likeCount: Integer,
        @JsonProperty("is_like")
        val isLike: Boolean,
        @JsonProperty("start_date")
        val startDate: String,
        @JsonProperty("end_date")
        val endDate: String,
        @JsonProperty("region")
        val region: String,
        @JsonProperty("phone")
        val phone: String,
        @JsonProperty("fee")
        val fee: Integer,
        @JsonProperty("introduce")
        val introduce: String
    )

}