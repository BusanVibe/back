package busanVibe.busan.domain.place.dto

import com.fasterxml.jackson.annotation.JsonProperty
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
        val id: Long?,
        val name: String,
        val congestionLevel: Int,
        @get:JsonProperty("is_like")
        val isLike: Boolean,
        val likeAmount: Int,
        val type: String,
        val address: String,
        val img: String?
    ){

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    sealed class PlaceDetailsDto {
        abstract val id: Long?

        abstract val name: String

        abstract val type: String

        abstract val img: List<String>?

        abstract val congestionLevel: Int

        abstract val likeAmount: Int

        @get:JsonProperty("is_open")
        abstract val isOpen: Boolean

        abstract val address: String

        abstract val phone: String

        @get:JsonProperty("is_like")
        abstract val isLike: Boolean

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class SightDto(
            override val id: Long?,
            override val name: String,
            override val type: String,
            override val img: List<String>,
            override val congestionLevel: Int,
            override val likeAmount: Int,
            @get:JsonProperty("is_open")
            override val isOpen: Boolean,
            override val address: String,
            override val phone: String,
            @get:JsonProperty("is_like")
            override val isLike: Boolean,
            val introduce: String,
            val useTime: String,
            val restDate: String
        ) : PlaceDetailsDto()

    }


    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class LikeDto(
        val success: Boolean,
        val message: String,
    )

}