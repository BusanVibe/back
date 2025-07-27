package busanVibe.busan.domain.place.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

class PlaceResponseDTO {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceListDto(
        val placeList: List<PlaceListInfoDto>
    ){

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class PlaceListInfoDto(
        val placeId: Long?,
        val name: String,
        val congestionLevel: Int,
        val isLike: Boolean,
        val likeAmount: Int,
        val type: String,
        val address: String,
        val img: String
    ){

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    sealed class PlaceDetailsDto {
        abstract val id: Long

        abstract val name: String

        abstract val type: String

        abstract val img: String

        abstract val congestionLevel: Int

        abstract val grade: Double

        abstract val reviewAmount: Int

        abstract val likeAmount: Int

        abstract val isOpen: Boolean

        abstract val address: String

        abstract val phone: String

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class SightDto(
            override val id: Long,
            override val name: String,
            override val type: String,
            override val img: String,
            override val congestionLevel: Int,
            override val grade: Double,
            override val reviewAmount: Int,
            override val likeAmount: Int,
            override val isOpen: Boolean,
            override val address: String,
            override val phone: String,
            val introduce: String
        ) : PlaceDetailsDto()

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class RestaurantDto(
            override val id: Long,
            override val name: String,
            override val type: String,
            override val img: String,
            override val congestionLevel: Int,
            override val grade: Double,
            override val reviewAmount: Int,
            override val likeAmount: Int,
            override val isOpen: Boolean,
            override val address: String,
            override val phone: String,
            val monOpen: String,
            val tueOpen: String,
            val wedOpen: String,
            val thuOpen: String,
            val friOpen: String,
            val satOpen: String,
            val sunOpen: String,
            val monClose: String,
            val tueClose: String,
            val wedClose: String,
            val thuClose: String,
            val friClose: String,
            val satClose: String,
            val sunClose: String,
            val review: List<ReviewDto>
        ) : PlaceDetailsDto()
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ReviewDto(
        val usrImg: String,
        val usrName: String,
        val grade: Double,
        val date: String,
        val content: String
    )

}