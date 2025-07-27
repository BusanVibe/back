package busanVibe.busan.domain.place.service

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.dto.PlaceResponseDTO
import busanVibe.busan.domain.place.enums.PlaceSortType
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceImageRepository
import busanVibe.busan.domain.place.repository.PlaceLikeRepository
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.service.login.AuthService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceQueryService(
    private val placeRepository: PlaceRepository,
    private val placeLikeRepository: PlaceLikeRepository,
    private val placeImageRepository: PlaceImageRepository,
    private val redisTemplate: StringRedisTemplate,
) {

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
        val congestionMap: Map<Long, Int> = placeIdList.associateWith { getRedisCongestion(it) }

        // DTO 변환
        val dtoList: List<PlaceResponseDTO.PlaceListInfoDto> = placeList.map { place ->
            val placeId = place.id!!
            PlaceResponseDTO.PlaceListInfoDto(
                placeId = placeId,
                name = place.name,
                congestionLevel = congestionMap[placeId] ?: 1,
                isLike = userLikedPlaceIds.contains(placeId),  // 현재 로그인한 사용자 기준
                likeAmount = likeCountMap[placeId] ?: 0,       // 전체 사용자 기준
                type = place.type.korean,
                address = place.address,
                img = placeImages[placeId] ?: ""
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


    // 임의로 혼잡도 생성하여 반환. 레디스 키 값으로 저장함.
    private fun getRedisCongestion(placeId: Long?): Int{

        val key = "place:congestion:$placeId"
        val randomCongestion: Int = (Math.random() * 5 + 1).toInt()

        redisTemplate.opsForValue()
            .set(key, randomCongestion.toString())

        return randomCongestion
    }


}