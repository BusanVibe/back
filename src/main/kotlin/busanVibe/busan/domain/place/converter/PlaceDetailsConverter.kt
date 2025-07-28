package busanVibe.busan.domain.place.converter

import busanVibe.busan.domain.place.domain.OpenTime
import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceImage
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.dto.PlaceResponseDTO
import busanVibe.busan.domain.place.service.PlaceRedisUtil
import busanVibe.busan.domain.review.domain.Review
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PlaceDetailsConverter(
    private val redisUtil: PlaceRedisUtil
){

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // 확장 함수: LocalTime -> "HH:mm" 문자열
    private fun LocalTime?.toTimeString(): String = this?.format(timeFormatter) ?: "unknown"

    // 관광지 DTO 변환
    fun toSightDto(
        place: Place,
        placeLikes: List<PlaceLike>,
        placeReviews: List<Review>,
        placeImages: List<PlaceImage>,
        isLike: Boolean
    ): PlaceResponseDTO.PlaceDetailsDto.SightDto = PlaceResponseDTO.PlaceDetailsDto.SightDto(
        id = place.id,
        name = place.name,
        type = place.type.korean,
        img = placeImages.firstOrNull()?.imgUrl,
        congestionLevel = redisUtil.getRedisCongestion(place.id),
        grade = if (placeReviews.isNotEmpty()) {
            placeReviews.map { it.score }.average().toFloat()
        } else {
            null
        },
        reviewAmount = placeReviews.size,
        likeAmount = placeLikes.size,
        address = place.address,
        isLike = isLike,
        phone = place.phone,
        introduce = place.introduction,
        isOpen = true // TODO: 실제 오픈 여부 판단
    )

    // 맛집 DTO 변환
    fun toRestaurantDto(
        place: Place,
        placeLikes: List<PlaceLike>,
        placeReviews: List<Review>,
        placeImages: List<PlaceImage>,
        placeOpenTime: OpenTime?,
        isLike: Boolean
    ): PlaceResponseDTO.PlaceDetailsDto.RestaurantDto = PlaceResponseDTO.PlaceDetailsDto.RestaurantDto(
        id = place.id,
        name = place.name,
        type = place.type.korean,
        img = placeImages.firstOrNull()?.imgUrl,
        congestionLevel = redisUtil.getRedisCongestion(place.id),
        grade = placeReviews.map { it.score }.average().toFloat(),
        reviewAmount = placeReviews.size,
        likeAmount = placeLikes.size,
        address = place.address,
        isLike = isLike,
        phone = place.phone,
        monOpen = placeOpenTime?.monOpen.toTimeString(),
        tueOpen = placeOpenTime?.tueOpen.toTimeString(),
        wedOpen = placeOpenTime?.wedOpen.toTimeString(),
        thuOpen = placeOpenTime?.thuOpen.toTimeString(),
        friOpen = placeOpenTime?.friOpen.toTimeString(),
        satOpen = placeOpenTime?.satOpen.toTimeString(),
        sunOpen = placeOpenTime?.sunOpen.toTimeString(),
        monClose = placeOpenTime?.monClose.toTimeString(),
        tueClose = placeOpenTime?.tueClose.toTimeString(),
        wedClose = placeOpenTime?.wedClose.toTimeString(),
        thuClose = placeOpenTime?.thuClose.toTimeString(),
        friClose = placeOpenTime?.friClose.toTimeString(),
        satClose = placeOpenTime?.satClose.toTimeString(),
        sunClose = placeOpenTime?.sunClose.toTimeString(),
        review = toReviewDtoList(placeReviews),
        isOpen = true // TODO: 실제 오픈 여부 판단
    )

    private fun toReviewDtoList(reviews: List<Review>): List<PlaceResponseDTO.ReviewDto> =
        reviews.map {
            PlaceResponseDTO.ReviewDto(
                usrName = it.user.nickname,
                usrImg = it.user.profileImageUrl,
                grade = it.score,
                date = it.createdAt.toString(),
                content = it.content
            )
        }
}
