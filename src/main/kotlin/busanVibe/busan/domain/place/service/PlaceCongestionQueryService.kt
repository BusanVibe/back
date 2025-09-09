package busanVibe.busan.domain.place.service

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceImage
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.domain.VisitorDistribution
import busanVibe.busan.domain.place.dto.PlaceMapResponseDTO
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.place.repository.VisitorDistributionRepository
import busanVibe.busan.domain.place.util.PlaceRedisUtil
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.GeneralException
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
    private val visitorDistributionRepository: VisitorDistributionRepository,
) {

    private val latitudeRange: Double = 0.05
    private val longitudeRange: Double = 0.05

    private val log = LoggerFactory.getLogger(PlaceCongestionQueryService::class.java)

    /**
     * 두 좌표를 받아 계산 및 조회 후 반환
     * 좌측상단 좌표 ( lat1, ln1 ), 우측하단 좌표 ( lat2, lng2 )
     * lat1 > lat2
     * lng1 < lng2
     */
    @Transactional(readOnly = true)
    fun getMap(type: PlaceType?, lat1: Double, lng1: Double, lat2: Double, lng2: Double): PlaceMapResponseDTO.MapListDto{

        // Place -> name, type, latitude, longitude
        // Redis -> congestion level

        if (lat1 !in -90.0..90.0 || lat2 !in -90.0..90.0)
            throw GeneralException(ErrorStatus.MAP_LATITUDE_OUT_OF_RANGE)

        if (lng1 !in -180.0..180.0 || lng2 !in -180.0..180.0)
            throw GeneralException(ErrorStatus.MAP_LONGITUDE_OUT_OF_RANGE)

        if (lat1 <= lat2 || lng1 >= lng2)
            throw GeneralException(ErrorStatus.MAP_INVALID_COORDINATE_ORDER)

        // Place 목록 조회
        val placeList = placeRepository.findPlacesByLocationAndType(
            BigDecimal(lat2).setScale(6, RoundingMode.HALF_UP),
            BigDecimal(lat1).setScale(6, RoundingMode.HALF_UP),
            BigDecimal(lng1).setScale(6, RoundingMode.HALF_UP),
            BigDecimal(lng2).setScale(6, RoundingMode.HALF_UP),
            type
        )

        // DTO 변환
        val placeDtoList :List<PlaceMapResponseDTO.PlaceMapInfoDto> = placeList.map {
            PlaceMapResponseDTO.PlaceMapInfoDto(
                id = it.id,
                name = it.name,
                type = it.type.capitalEnglish,
                latitude = it.latitude,
                longitude = it.longitude,
                congestionLevel = placeRedisUtil.getTimeCongestion(it.id).toInt()
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

        // 유저 조회
        val currentUser = AuthService().getCurrentUser()

        // 명소 조회
        val place: Place? = placeRepository.findByIdWithLIkeAndImage(placeId)
        place?: throw ExceptionHandler(ErrorStatus.PLACE_NOT_FOUND)

        // 이미지 조회
        val placeImageSet: Set<PlaceImage> = place.placeImages
        val placeImageList = placeImageSet.toList()
            .sortedBy { it.createdAt }
            .filter { it.imgUrl.isNotBlank() }
            .map { it.imgUrl }

        // 좋아요 조회
        val placeLikes: Set<PlaceLike> = place.placeLikes
        val isLike = placeLikes.any { it.user.id == currentUser.id }

        return PlaceMapResponseDTO.PlaceDefaultInfoDto(
            id = place.id,
            name = place.name,
            congestionLevel = placeRedisUtil.getTimeCongestion(place.id).toInt(),
            latitude = place.latitude,
            longitude = place.longitude,
            address = place.address,
            isOpen = true,
            imgList = placeImageList,
            isLike = isLike
        )
    }

    @Transactional(readOnly = true)
    fun getCongestion(placeId: Long): PlaceMapResponseDTO.PlaceCongestionDto {

        // 현재시간 기준 LocalDateTime
        val currentDateTime = LocalDateTime.now()

        // 혼잡도 리스트
        val congestionsByDay: MutableList<Float> = mutableListOf() // 요일별
        val congestionsByTime: MutableList<List<Float>> = mutableListOf() // 시간대별

        // 시간대별 혼잡도 ( 월~일, 0~23시 )
        for( day in 1..7){
            val congestions = mutableListOf<Float>() // 각 요일의 혼잡도 정보 담을 List
            for( hour in 0..23){
                congestions.add(placeRedisUtil.getTimeCongestion(placeId, day, hour))
            }
            congestionsByTime.add(congestions)
        }

        // 요일별 현재 시간의 혼잡도
        for( day in 1..7){
            congestionsByDay.add(placeRedisUtil.getTimeCongestion(placeId, day, currentDateTime.hour))
        }

        // DTO 생성 및 반환
        return PlaceMapResponseDTO.PlaceCongestionDto(
            standardDay = currentDateTime.dayOfWeek.value-1, // 원래 1:월~7:일인데, 0:월~6일로 변경해서 반환
            standardTime = currentDateTime.hour, // 0~23
            realTimeCongestionLevel = placeRedisUtil.getTimeCongestion(placeId).toInt(),
            congestionsByDay = congestionsByDay,
            congestionsByTime = congestionsByTime
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