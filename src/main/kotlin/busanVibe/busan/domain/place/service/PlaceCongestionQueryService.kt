package busanVibe.busan.domain.place.service

import busanVibe.busan.domain.place.dto.PlaceMapResponseDTO
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class PlaceCongestionQueryService(
    private val placeRepository: PlaceRepository,
    private val placeRedisUtil: PlaceRedisUtil
) {

    private val latitudeRange: Double = 0.05
    private val longitudeRange: Double = 0.05

    @Transactional(readOnly = true)
    fun getMap(type: PlaceType?, latitude: Double, longitude: Double): PlaceMapResponseDTO.MapListDto{

        // Place -> name, type, latitude, longitude
        // Redis -> congestion level

        // Place 목록 조회
        val placeList = placeRepository.findPlacesByLocationAndType(
            BigDecimal(latitude - latitudeRange).setScale(6, RoundingMode.HALF_UP),
            BigDecimal(latitude + latitudeRange).setScale(6, RoundingMode.HALF_UP),
            BigDecimal(longitude - longitudeRange).setScale(6, RoundingMode.HALF_UP),
            BigDecimal(longitude + longitudeRange).setScale(6, RoundingMode.HALF_UP),
            type
        )

        // DTO 변환
        val placeDtoList :List<PlaceMapResponseDTO.PlaceMapInfoDto> = placeList.map {
            PlaceMapResponseDTO.PlaceMapInfoDto(
                placeId = it.id,
                name = it.name,
                type = it.type.capitalEnglish,
                latitude = it.latitude,
                longitude = it.longitude,
                congestionLevel = placeRedisUtil.getRedisCongestion(it.id)
            )
        }

        return PlaceMapResponseDTO.MapListDto(placeDtoList)
    }

}