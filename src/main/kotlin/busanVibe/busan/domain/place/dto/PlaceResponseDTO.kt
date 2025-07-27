package busanVibe.busan.domain.place.dto

import com.fasterxml.jackson.annotation.JsonProperty

class PlaceResponseDTO {

    data class PlaceListDto(
        @JsonProperty("place_list")
        val placeList: List<PlaceListInfoDto>
    ){

    }

    data class PlaceListInfoDto(
        @JsonProperty("place_id")
        val placeId: Long,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("congestion_level")
        val congestionLevel: Integer,
        @JsonProperty("is_like")
        val isLike: Boolean,
        @JsonProperty("type")
        val type: String,
        @JsonProperty("address")
        val address: String,
        @JsonProperty("img")
        val img: String
    ){

    }

    sealed class PlaceDetailsDto {
        @get:JsonProperty("id")
        abstract val id: Long

        @get:JsonProperty("name")
        abstract val name: String

        @get:JsonProperty("type")
        abstract val type: String

        @get:JsonProperty("img")
        abstract val img: String

        @get:JsonProperty("congestion_level")
        abstract val congestionLevel: Int

        @get:JsonProperty("grade")
        abstract val grade: Double

        @get:JsonProperty("review_amount")
        abstract val reviewAmount: Int

        @get:JsonProperty("like_amount")
        abstract val likeAmount: Int

        @get:JsonProperty("is_open")
        abstract val isOpen: Boolean

        @get:JsonProperty("address")
        abstract val address: String

        @get:JsonProperty("phone")
        abstract val phone: String

        data class SightDto(
            @JsonProperty("id")
            override val id: Long,
            @JsonProperty("name")
            override val name: String,
            @JsonProperty("type")
            override val type: String,
            @JsonProperty("img")
            override val img: String,
            @JsonProperty("congestion_level")
            override val congestionLevel: Int,
            @JsonProperty("grade")
            override val grade: Double,
            @JsonProperty("review_amount")
            override val reviewAmount: Int,
            @JsonProperty("like_amount")
            override val likeAmount: Int,
            @JsonProperty("is_open")
            override val isOpen: Boolean,
            @JsonProperty("address")
            override val address: String,
            @JsonProperty("phone")
            override val phone: String,
            @JsonProperty("introduce")
            val introduce: String
        ) : PlaceDetailsDto()

        data class RestaurantDto(
            @JsonProperty("id")
            override val id: Long,
            @JsonProperty("name")
            override val name: String,
            @JsonProperty("type")
            override val type: String,
            @JsonProperty("img")
            override val img: String,
            @JsonProperty("congestion_level")
            override val congestionLevel: Int,
            @JsonProperty("grade")
            override val grade: Double,
            @JsonProperty("review_amount")
            override val reviewAmount: Int,
            @JsonProperty("like_amount")
            override val likeAmount: Int,
            @JsonProperty("is_open")
            override val isOpen: Boolean,
            @JsonProperty("address")
            override val address: String,
            @JsonProperty("phone")
            override val phone: String,
            @JsonProperty("mon_open")
            val monOpen: String,
            @JsonProperty("tue_open")
            val tueOpen: String,
            @JsonProperty("wed_open")
            val wedOpen: String,
            @JsonProperty("thu_open")
            val thuOpen: String,
            @JsonProperty("fri_open")
            val friOpen: String,
            @JsonProperty("sat_open")
            val satOpen: String,
            @JsonProperty("sun_open")
            val sunOpen: String,
            @JsonProperty("mon_close")
            val monClose: String,
            @JsonProperty("tue_close")
            val tueClose: String,
            @JsonProperty("wed_close")
            val wedClose: String,
            @JsonProperty("thu_close")
            val thuClose: String,
            @JsonProperty("fri_close")
            val friClose: String,
            @JsonProperty("sat_close")
            val satClose: String,
            @JsonProperty("sun_close")
            val sunClose: String,
            @JsonProperty("review")
            val review: List<ReviewDto>
        ) : PlaceDetailsDto()
    }

    data class ReviewDto(
        @JsonProperty("usr_img")
        val usrImg: String,
        @JsonProperty("usr_name")
        val usrName: String,
        @JsonProperty("grade")
        val grade: Double,
        @JsonProperty("date")
        val date: String,
        @JsonProperty("content")
        val content: String
    )

}