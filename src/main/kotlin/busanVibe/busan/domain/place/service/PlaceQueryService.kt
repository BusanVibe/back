package busanVibe.busan.domain.place.service

import busanVibe.busan.domain.place.converter.PlaceDetailsConverter
import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceImage
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.dto.PlaceResponseDTO
import busanVibe.busan.domain.place.enums.PlaceSortType
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.OpenTimeRepository
import busanVibe.busan.domain.place.repository.PlaceImageRepository
import busanVibe.busan.domain.place.repository.PlaceLikeRepository
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.review.domain.Review
import busanVibe.busan.domain.review.domain.repository.ReviewRepository
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.String

@Service
class PlaceQueryService(
    private val placeRepository: PlaceRepository,
    private val placeLikeRepository: PlaceLikeRepository,
    private val placeImageRepository: PlaceImageRepository,
    private val reviewRepository: ReviewRepository,
    private val redisTemplate: StringRedisTemplate,
) {

    private val redisUtil = PlaceRedisUtil(redisTemplate)
    private val placeDetailsConverter = PlaceDetailsConverter(redisUtil)
    private val log = LoggerFactory.getLogger(PlaceQueryService::class.java)

    @Transactional(readOnly = true)
    fun getPlaceList(category: PlaceType?, sort: PlaceSortType?): PlaceResponseDTO.PlaceListDto {

        val currentUser: User = AuthService().getCurrentUser()

        val category = category ?: PlaceType.ALL
        val sort = sort ?: PlaceSortType.DEFAULT

        val placeList: List<Place> = when (category) {
            PlaceType.ALL -> placeRepository.findAll()
            else -> placeRepository.findByType(category)
        }

        val placeIdList: List<Long> = placeList.mapNotNull { it.id }

        // 좋아요 리스트 조회
        val placeLikes: List<PlaceLike> = placeLikeRepository.findAllByPlaceIn(placeList)

        // 좋아요 수 계산: Map<Long, Int>
        val likeCountMap: Map<Long, Int> = placeLikes
            .groupingBy { it.place.id!! }
            .eachCount()

        // 사용자의 좋아요 여부를 판단하기 위해 Set으로 보유
        val userLikedPlaceIds: Set<Long> = placeLikes
            .filter { it.user.id == currentUser.id }
            .map { it.place.id!! }
            .toSet()

        // 이미지 조회
        val placeImages: Map<Long, String> = placeImageRepository.findByPlaceIn(placeList)
            .associateBy({ it.place.id!! }, { it.imgUrl })

        // 혼잡도 조회
        val placeRedisUtil = PlaceRedisUtil(redisTemplate)
        val congestionMap: Map<Long, Int> = placeIdList.associateWith { placeRedisUtil.getRedisCongestion(it) }

        // DTO 변환
        val dtoList: List<PlaceResponseDTO.PlaceListInfoDto> = placeList.map { place ->
            val placeId = place.id!!
            PlaceResponseDTO.PlaceListInfoDto(
                placeId = placeId,
                name = place.name,
                congestionLevel = congestionMap[placeId] ?: 1,
                isLike = userLikedPlaceIds.contains(placeId),  // 현재 로그인한 사용자 기준
                likeAmount = likeCountMap[placeId] ?: 0,       // 전체 사용자 기준
                type = place.type.capitalEnglish,
                address = place.address,
                img = placeImages[placeId]
            )
        }


        // 정렬 처리
        val sortedList = when (sort) {
            PlaceSortType.DEFAULT -> dtoList
            PlaceSortType.LIKES -> dtoList.sortedByDescending { it.likeAmount }
            PlaceSortType.CONGESTION -> dtoList.sortedByDescending { it.congestionLevel }
        }

        return PlaceResponseDTO.PlaceListDto(sortedList)
    }

    @Transactional(readOnly = true)
    fun getPlaceDetails(placeId: Long): PlaceResponseDTO.PlaceDetailsDto {

        val currentUser: User = AuthService().getCurrentUser()

        // 관광지일 때는 요일별 오픈시간도 같이 조회
        log.info(" 명소 + 이미지 + 좋아요 + 오픈시간 조회")
        val place: Place? = placeRepository.findByIdWithLIkeAndImage(placeId)
        // 검증
        place ?: throw ExceptionHandler(ErrorStatus.PLACE_NOT_FOUND)

        // 필요한 연관관계 객체 리스트 추출
        val placeLikes: List<PlaceLike> = place.placeLikes.toList()
        val placeImages: List<PlaceImage> = place.placeImages.toList()

        log.info("장소 리뷰 조회")
        val placeReviews: List<Review> = reviewRepository.findForDetails(place)

        // 좋아요 여부 구함
        val isLike = placeLikes.any { it.user.id == currentUser.id }

        if(place.type == PlaceType.SIGHT){
            return placeDetailsConverter.toSightDto(
                place = place,
                placeLikes = placeLikes,
                placeReviews = placeReviews,
                placeImages = placeImages,
                isLike
            )
        }
        else{
            return placeDetailsConverter.toRestaurantDto(
                place = place,
                placeLikes = placeLikes,
                placeReviews = placeReviews,
                placeImages = placeImages,
                placeOpenTime = place.openTime,
                isLike
            )
        }

    }


}