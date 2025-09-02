package busanVibe.busan.domain.place.converter

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceImage
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.dto.PlaceResponseDTO
import busanVibe.busan.domain.place.util.PlaceRedisUtil
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
        placeImages: List<PlaceImage>,
        isLike: Boolean
    ): PlaceResponseDTO.PlaceDetailsDto.SightDto = PlaceResponseDTO.PlaceDetailsDto.SightDto(
        id = place.id,
        name = place.name,
        type = place.type.capitalEnglish,
        img = placeImages.map { it.imgUrl },
        congestionLevel = redisUtil.getRedisCongestion(place.id),
        likeAmount = placeLikes.size,
        address = place.address,
        isLike = isLike,
        phone = place.phone,
        introduce = place.introduction,
        isOpen = true, // TODO: 실제 오픈 여부 판단
        useTime = place.useTime,
        restDate = place.restDate,
    )

}
