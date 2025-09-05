package busanVibe.busan.domain.home.service

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.home.converter.CurationConverter
import busanVibe.busan.domain.home.dto.HomeResponseDTO
import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.place.util.PlaceRedisUtil
import busanVibe.busan.domain.place.util.nullIfBlank
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomeQueryService(
    private val placeRepository: PlaceRepository,
    private val placeRedisUtil: PlaceRedisUtil,
    private val festivalRepository: FestivalRepository
) {

    @Transactional(readOnly = true)
    fun getHomeInfo(): HomeResponseDTO.HomeResultDto{

        // 조회 메서드 호출
        val mostCongestions:List<HomeResponseDTO.MostCongestion> = getMostCongestions()
        val recommendPlaces:List<HomeResponseDTO.RecommendPlace> = getRecommendPlaces()

        // 반환
        return HomeResponseDTO.HomeResultDto(
            mostCongestions, recommendPlaces
        )
    }

    @Transactional(readOnly = true)
    fun getCurations(): HomeResponseDTO.CurationList{

        // 자원 생성
        val placeCount = 1
        val festivalCount = 2

        // 명소 조회
        val placeList: List<Place> = placeRepository.findPlaceImageNotNull() // 이미지 데이터가 있는 Place 목록 조회
        val randomPlace: List<Place> = placeList
            .takeIf { it.size >= placeCount }
            ?.shuffled()
            ?.take(placeCount)
            ?: throw ExceptionHandler(ErrorStatus.PLACE_NOT_FOUND) // 그 중 랜덤한 항목 가져옴. list 비어있을 시 예외처리


        // 축제 조회
        val festivalList = festivalRepository.findFestivalImageNotNull() // 이미지 데이터가 있는 Festival 목록 조회
        val randomFestival: List<Festival> = festivalList
                .takeIf { it.size >= festivalCount }
                ?.shuffled()
                ?.take(festivalCount)
                ?: throw ExceptionHandler(ErrorStatus.FESTIVAL_NOT_FOUND) // 그 중 랜덤한 항목 가져옴. list 비어있을 시 예외처리

        // DTO 생성 및 반환
        return CurationConverter().toListDto(randomPlace, randomFestival)
    }

    // 가장 붐비는 곳 조회 하여 List<DTO> 반환 - 5개
    private fun getMostCongestions(): List<HomeResponseDTO.MostCongestion>{

        val places = placeRepository.findAllWithImages()
        val placesWithCongestion = places.mapNotNull { place ->
            val congestion = placeRedisUtil.getRedisCongestion(place.id)
            if (congestion != null) {
                place to congestion
            } else {
                null
            }
        }

        val top5 = placesWithCongestion.sortedByDescending { it.second }.take(5)

        return top5.map { (place, congestion) ->
            HomeResponseDTO.MostCongestion(
                id = place.id,
                name = place.name,
                latitude = place.latitude?.toDouble(),
                longitude = place.longitude?.toDouble(),
                type = place.type.korean,
                image = place.placeImages.firstOrNull()?.imgUrl.nullIfBlank(),
                congestionLevel = congestion,
                address = place.address
            )
        }

    }

    // 추천 명소 조회하여 List<DTO> 반환 - 5개
    private fun getRecommendPlaces(): List<HomeResponseDTO.RecommendPlace> {
        val currentUser = AuthService().getCurrentUser()
        val places = placeRepository.findAllWithFetch()

        return places.shuffled().take(5).map { place ->

            val congestion = placeRedisUtil.getRedisCongestion(place.id)

            HomeResponseDTO.RecommendPlace(
                id = place.id,
                name = place.name,
                congestionLevel = congestion,
                type = place.type.korean,
                image = place.placeImages.firstOrNull()?.imgUrl.nullIfBlank(),
                latitude = place.latitude?.toDouble(),
                longitude = place.longitude?.toDouble(),
                address = place.address,
                isLike = place.placeLikes.any { it.user.id == currentUser.id }
            )
        }
    }


}