package busanVibe.busan.domain.place.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming


class PlaceMapResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MapListDto(
        val placeList: List<PlaceMapInfoDto>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceMapInfoDto(
        val placeId: Long,
        val name: String,
        val type: String,
        val congestionLevel: Integer,
        val latitude: Double,
        val longitude: Double
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceDefaultInfoDto(
        val id: Long,
        val name: String,
        val congestionLevel: Integer,
        val grade: Float,
        val reviewAmount: Integer,
        val latitude: Double,
        val longitude: Double,
        val address: String,
        @get:JsonProperty("is_open")
        val isOpen: Boolean,
        val imgList: List<String>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceCongestionDto(
        val standardTime: String,
        val realTimeCongestionLevel: Integer,
        val byTimePercent: List<Float>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceUserDistributionDto(
        val male1020: Float,
        val male3040: Float,
        val male5060: Float,
        val male70: Float,
        val female1020: Float,
        val female3040: Float,
        val female5060: Float,
        val female70: Float,
    )

}