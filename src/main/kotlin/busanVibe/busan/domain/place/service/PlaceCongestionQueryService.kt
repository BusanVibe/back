package busanVibe.busan.domain.place.service

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceImage
import busanVibe.busan.domain.place.domain.VisitorDistribution
import busanVibe.busan.domain.place.dto.PlaceMapResponseDTO
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.place.repository.VisitorDistributionRepository
import busanVibe.busan.domain.review.domain.Review
import busanVibe.busan.domain.review.domain.repository.ReviewRepository
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class PlaceCongestionQueryService(
    private val placeRepository: PlaceRepository,
    private val placeRedisUtil: PlaceRedisUtil,
    private val reviewRepository: ReviewRepository,
    private val visitorDistributionRepository: VisitorDistributionRepository,
) {

    private val latitudeRange: Double = 0.05
    private val longitudeRange: Double = 0.05

    private val log = LoggerFactory.getLogger(PlaceCongestionQueryService::class.java)

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

    @Transactional(readOnly = true)
    fun getPlaceDefault(placeId: Long): PlaceMapResponseDTO.PlaceDefaultInfoDto{

        // Place -> name, imageList, address, openTime
        // Review -> grade, count
        // Image -> list
        // Redis -> congestion

        // 명소 조회
        val place: Place? = placeRepository.findByIdWithReviewAndImage(placeId)
        place?: throw ExceptionHandler(ErrorStatus.PLACE_NOT_FOUND)

        // 이미지 조회
        val placeImageSet: Set<PlaceImage> = place.placeImages
        val placeImageList = placeImageSet.toList()
            .sortedBy { it.createdAt }
            .map { it.imgUrl }

        // 리뷰 조회
        val reviewSet: Set<Review> = reviewRepository.findByPlace(place).toSet()
        val grade = if (reviewSet.isEmpty()) 0f 
                    else reviewSet.map { it.score }.average().toFloat()

        return PlaceMapResponseDTO.PlaceDefaultInfoDto(
            id = place.id,
            name = place.name,
            congestionLevel = placeRedisUtil.getRedisCongestion(place.id),
            grade = grade,
            reviewAmount = reviewSet.size,
            latitude = place.latitude,
            longitude = place.longitude,
            address = place.address,
            isOpen = true,
            imgList = placeImageList
        )
    }

    @Transactional(readOnly = true)
    fun getCongestion(placeId: Long): PlaceMapResponseDTO.PlaceCongestionDto {

        val current = LocalDateTime.now()
        log.info("현재 시간: ${current}시")

        val roundedBase = (current.hour / 3) * 3

        // 최근 6개 3시간 단위 시간 생성 (기준시간 포함 총 7개)
        val hours = (6 downTo 0).map { i -> (roundedBase - i * 3 + 24) % 24 }

        log.info("배열 담기 시작")

        val byTimePercent: List<Float> = hours.map { hour ->
            val adjustedDateTime = current.withHour(hour)
                .withMinute(0).withSecond(0).withNano(0)
                .let {
                    if (hour > current.hour) it.minusDays(1) else it
                }
            placeRedisUtil.getTimeCongestion(placeId, adjustedDateTime)
        }

        log.info("배열 담기 끝")

        return PlaceMapResponseDTO.PlaceCongestionDto(
            standardTime = roundedBase,
            realTimeCongestionLevel = placeRedisUtil.getTimeCongestion(placeId, current).toInt(),
            byTimePercent = byTimePercent
        )
    }

    @Transactional(readOnly = false)
    fun getDistribution(placeId: Long): PlaceMapResponseDTO.PlaceUserDistributionDto{

        val place = placeRepository.findWithDistribution(placeId)
            .orElseThrow { ExceptionHandler(ErrorStatus.PLACE_NOT_FOUND) }

        val distribution: VisitorDistribution = place.visitorDistribution
            ?: visitorDistributionRepository.saveAndFlush<VisitorDistribution>(VisitorDistribution())

        val totalVisitorCount: Float = distribution.getTotalVisitorCount().toFloat()

        return PlaceMapResponseDTO.PlaceUserDistributionDto(
            male1020 = safePercent(distribution.m1020, totalVisitorCount),
            male3040 = safePercent(distribution.m3040, totalVisitorCount),
            male5060 = safePercent(distribution.m5060, totalVisitorCount),
            male70 = safePercent(distribution.m70, totalVisitorCount),
            female1020 = safePercent(distribution.f1020, totalVisitorCount),
            female3040 = safePercent(distribution.f3040, totalVisitorCount),
            female5060 = safePercent(distribution.f5060, totalVisitorCount),
            female70 = safePercent(distribution.f70, totalVisitorCount)
        )

    }

    private fun safePercent(numerator: Int, denominator: Float): Float{
        return if(denominator == 0.0f) 0f else numerator / denominator * 100.0f
    }



}