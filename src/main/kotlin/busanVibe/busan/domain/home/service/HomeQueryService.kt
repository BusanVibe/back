package busanVibe.busan.domain.home.service

import busanVibe.busan.domain.home.dto.HomeResponseDTO
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.place.util.PlaceRedisUtil
import busanVibe.busan.domain.user.service.login.AuthService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomeQueryService(
    private val placeRepository: PlaceRepository,
    private val placeRedisUtil: PlaceRedisUtil
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
                image = place.placeImages.firstOrNull()?.imgUrl,
                congestionLevel = congestion,
                address = place.address
            )
        }

    }

    // 추천 명소 조회하여 List<DTO> 반환 - 5개
    private fun getRecommendPlaces(): List<HomeResponseDTO.RecommendPlace> {
        val currentUser = AuthService().getCurrentUser()
        val places = placeRepository.findAllWithFetch()

        return places.take(5).map { place ->

            val congestion = placeRedisUtil.getRedisCongestion(place.id)

            HomeResponseDTO.RecommendPlace(
                id = place.id,
                name = place.name,
                congestionLevel = congestion,
                type = place.type.korean,
                image = place.placeImages.firstOrNull()?.imgUrl,
                latitude = place.latitude?.toDouble(),
                longitude = place.longitude?.toDouble(),
                address = place.address,
                isLike = place.placeLikes.any { it.user == currentUser }
            )
        }
    }


}