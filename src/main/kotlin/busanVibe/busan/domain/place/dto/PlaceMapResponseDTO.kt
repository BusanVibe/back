package busanVibe.busan.domain.place.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal


class PlaceMapResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MapListDto(
        val placeList: List<PlaceMapInfoDto>
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceMapInfoDto(
        val id: Long?,
        val name: String,
        val type: String,
        val congestionLevel: Int,
        val latitude: BigDecimal? = null,
        val longitude: BigDecimal? = null
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceDefaultInfoDto(
        val id: Long?,
        val name: String,
        val congestionLevel: Int,
        val latitude: BigDecimal?,
        val longitude: BigDecimal?,
        val address: String,
        @get:JsonProperty("is_open")
        val isOpen: Boolean,
        val imgList: List<String>,
        val isLike: Boolean
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceCongestionDto(
        val standardTime: Int,
        val realTimeCongestionLevel: Int,
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