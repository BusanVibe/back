package busanVibe.busan.domain.place.dto

import com.fasterxml.jackson.annotation.JsonProperty


class PlaceMapResponseDTO {

    data class MapListDto(
        @JsonProperty("place_list")
        val placeList: List<PlaceMapInfoDto>
    )

    data class PlaceMapInfoDto(
        @JsonProperty("place_id")
        val placeId: Long,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("type")
        val type: String,
        @JsonProperty("congestion_level")
        val congestionLevel: Integer,
        @JsonProperty("latitude")
        val latitude: Double,
        @JsonProperty("longitude")
        val longitude: Double
    )

    data class PlaceDefaultInfoDto(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("congestion_level")
        val congestionLevel: Integer,
        @JsonProperty("grade")
        val grade: Float,
        @JsonProperty("review_amount")
        val reviewAmount: Integer,
        @JsonProperty("latitude")
        val latitude: Double,
        @JsonProperty("longitude")
        val longitude: Double,
        @JsonProperty("address")
        val address: String,
        @JsonProperty("is_open")
        val isOpen: Boolean,
        @JsonProperty("img_list")
        val imgList: List<String>
    )

    data class PlaceCongestionDto(
        @JsonProperty("standard_time")
        val standardTime: String,
        @JsonProperty("real_time_congestion_level")
        val realTimeCongestionLevel: Integer,
        @JsonProperty("by_time_percent")
        val byTimePercent: List<Float>
    )

    data class PlaceUserDistributionDto(
        @JsonProperty("male_1020")
        val male1020: Float,
        @JsonProperty("male_3040")
        val male3040: Float,
        @JsonProperty("male_5060")
        val male5060: Float,
        @JsonProperty("male_70")
        val male70: Float,
        @JsonProperty("female_1020")
        val female1020: Float,
        @JsonProperty("female_3040")
        val female3040: Float,
        @JsonProperty("female_5060")
        val female5060: Float,
        @JsonProperty("female_70")
        val female70: Float,
    )

}